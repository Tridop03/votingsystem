import React from 'react';
import CustomBarChart from '../../../components/charts/BarChart';
import CustomPieChart from '../../../components/charts/PieChart';

interface ResultsChartProps {
  data: any[];
  type: 'bar' | 'pie';
  title: string;
}

const ResultsChart: React.FC<ResultsChartProps> = ({ data, type, title }) => {
  return (
    <div className="card">
      <h3 className="font-bold text-gray-900 mb-6">{title}</h3>
      {type === 'bar' ? (
        <CustomBarChart data={data} />
      ) : (
        <CustomPieChart data={data} />
      )}
    </div>
  );
};

export default ResultsChart;
