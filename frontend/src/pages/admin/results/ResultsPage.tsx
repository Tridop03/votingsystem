import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { resultsAPI } from '../../../api/resultsAPI';
import { electionAPI } from '../../../api/electionAPI';
import PageWrapper from '../../../components/layout/PageWrapper';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import CustomBarChart from '../../../components/charts/BarChart';
import CustomPieChart from '../../../components/charts/PieChart';
import StatsCard from '../../../components/charts/StatsCard';
import { Vote, Users, Award, ArrowLeft, Download, FileText } from 'lucide-react';
import Button from '../../../components/common/Button';
import { downloadPDF, downloadCSV } from '../../../utils/exportHelper';
import { toast } from 'react-toastify';

const ResultsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [election, setElection] = useState<any>(null);
  const [results, setResults] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeCategory, setActiveCategory] = useState<number | null>(null);

  useEffect(() => {
    const fetchResultsData = async () => {
      if (!id) return;
      try {
        const [electionRes, resultsRes] = await Promise.all([
          electionAPI.getElectionById(id),
          resultsAPI.getResults(id)
        ]);
        setElection(electionRes.data);
        setResults(resultsRes.data);
        if (resultsRes.data.length > 0) {
          setActiveCategory(resultsRes.data[0].category_id);
        }
      } catch (error) {
        toast.error('Failed to load election results');
      } finally {
        setLoading(false);
      }
    };

    fetchResultsData();
  }, [id]);

  if (loading) return <LoadingSpinner fullPage />;
  if (!election) return <div className="p-8 text-center">Results not available.</div>;

  const currentCategoryResults = results.find(r => r.category_id === activeCategory);
  const chartData = currentCategoryResults?.candidates.map((c: any) => ({
    name: c.full_name,
    votes: c.vote_count,
    value: c.vote_count
  })) || [];

  const winner = currentCategoryResults?.candidates.reduce((prev: any, current: any) => 
    (prev.vote_count > current.vote_count) ? prev : current
  , currentCategoryResults?.candidates[0]);

  const handleExportPDF = async () => {
    try {
      const response = await resultsAPI.exportPDF(id!);
      downloadPDF(response.data, `${election.title}_Results`);
    } catch (error) {
      toast.error('Failed to export PDF');
    }
  };

  const handleExportCSV = async () => {
    try {
      const response = await resultsAPI.exportCSV(id!);
      downloadCSV(response.data, `${election.title}_Results`);
    } catch (error) {
      toast.error('Failed to export CSV');
    }
  };

  return (
    <PageWrapper
      title="Election Results"
      subtitle={`Detailed analysis for ${election.title}`}
      actions={
        <div className="flex gap-2">
          <Button variant="outline" size="sm" icon={<FileText size={16} />} onClick={handleExportCSV}>
            Export CSV
          </Button>
          <Button variant="primary" size="sm" icon={<Download size={16} />} onClick={handleExportPDF}>
            Export PDF
          </Button>
        </div>
      }
    >
      <Link to="/admin/elections" className="inline-flex items-center gap-2 text-sm font-bold text-gray-500 hover:text-indigo-600 mb-6 transition-colors">
        <ArrowLeft size={16} />
        Back to Elections
      </Link>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <StatsCard title="Total Votes" value={currentCategoryResults?.total_votes || 0} icon={Vote} color="indigo" />
        <StatsCard title="Turnout" value={`${currentCategoryResults?.turnout_percentage || 0}%`} icon={Users} color="blue" />
        <StatsCard title="Winner" value={winner?.full_name || 'N/A'} icon={Award} color="green" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-1 space-y-6">
          <div className="card">
            <h3 className="font-bold text-gray-900 mb-4">Categories</h3>
            <div className="space-y-2">
              {results.map((r: any) => (
                <button
                  key={r.category_id}
                  onClick={() => setActiveCategory(r.category_id)}
                  className={`w-full text-left px-4 py-3 rounded-xl text-sm font-medium transition-all ${
                    activeCategory === r.category_id
                      ? 'bg-indigo-600 text-white shadow-md'
                      : 'hover:bg-gray-50 text-gray-600'
                  }`}
                >
                  {r.category_name}
                </button>
              ))}
            </div>
          </div>

          <div className="card">
            <h3 className="font-bold text-gray-900 mb-4">Breakdown</h3>
            <div className="space-y-4">
              {currentCategoryResults?.candidates.map((c: any, idx: number) => (
                <div key={idx} className="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
                  <div>
                    <p className="font-bold text-gray-900">{c.full_name}</p>
                    <p className="text-xs text-gray-500">{c.party}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-indigo-600">{c.vote_count}</p>
                    <p className="text-[10px] text-gray-400 font-bold uppercase tracking-wider">
                      {((c.vote_count / (currentCategoryResults.total_votes || 1)) * 100).toFixed(1)}%
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="lg:col-span-2 space-y-6">
          <div className="card">
            <h3 className="font-bold text-gray-900 mb-6">Vote Distribution</h3>
            <CustomBarChart data={chartData} />
          </div>
          <div className="card">
            <h3 className="font-bold text-gray-900 mb-6">Vote Share</h3>
            <CustomPieChart data={chartData} />
          </div>
        </div>
      </div>
    </PageWrapper>
  );
};

export default ResultsPage;
