import React from 'react';

interface Column {
  header: string;
  accessor: string | ((item: any) => React.ReactNode);
  className?: string;
}

interface TableProps {
  columns: Column[];
  data: any[];
  loading?: boolean;
  emptyMessage?: string;
}

const Table: React.FC<TableProps> = ({ columns, data, loading, emptyMessage = 'No data available' }) => {
  if (loading) {
    return (
      <div className="w-full h-64 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (data.length === 0) {
    return (
      <div className="w-full h-64 flex flex-col items-center justify-center text-gray-500 bg-white rounded-2xl border border-dashed">
        <p>{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className="w-full overflow-x-auto bg-white rounded-2xl shadow-sm border border-gray-100">
      <table className="w-full text-left border-collapse">
        <thead>
          <tr className="bg-gray-50 border-b border-gray-100">
            {columns.map((col, idx) => (
              <th key={idx} className={`px-6 py-4 text-sm font-semibold text-gray-600 ${col.className || ''}`}>
                {col.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((item, rowIdx) => (
            <tr key={rowIdx} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
              {columns.map((col, colIdx) => (
                <td key={colIdx} className={`px-6 py-4 text-sm text-gray-700 ${col.className || ''}`}>
                  {typeof col.accessor === 'function' ? col.accessor(item) : item[col.accessor]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Table;
