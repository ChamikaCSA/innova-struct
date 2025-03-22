import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import CompanyProfile from '../../components/CompanyProfile/CompanyProfile';
import ClientNavbar from '../../components/ClientNavbar';
import companyService from '../../services/companyService';

const CompanyProfilePage = () => {
  const [isSidebarMinimized, setIsSidebarMinimized] = useState(false);
  const [company, setCompany] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { id } = useParams();

  useEffect(() => {
    const handleSidebarStateChange = (event) => {
      setIsSidebarMinimized(event.detail);
    };

    window.addEventListener('sidebarStateChange', handleSidebarStateChange);
    return () => {
      window.removeEventListener('sidebarStateChange', handleSidebarStateChange);
    };
  }, []);

  useEffect(() => {
    const fetchCompany = async () => {
      try {
        setLoading(true);
        const data = await companyService.getCompanyById(id);
        setCompany(data);
        setError(null);
      } catch (err) {
        console.error('Error fetching company:', err);
        setError('Failed to load company data');
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchCompany();
    }
  }, [id]);

  if (loading) {
    return (
      <div className="flex">
        <ClientNavbar />
        <div className={`flex-1 transition-all duration-300 ${
          isSidebarMinimized ? 'ml-20' : 'ml-80'
        } p-4`}>
          <div className="flex justify-center items-center h-64">
            <div className="loading loading-spinner loading-lg text-yellow-400"></div>
          </div>
        </div>
      </div>
    );
  }

  if (error || !company) {
    return (
      <div className="flex">
        <ClientNavbar />
        <div className={`flex-1 transition-all duration-300 ${
          isSidebarMinimized ? 'ml-20' : 'ml-80'
        } p-4`}>
          <div>{error || 'Company not found.'}</div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen">
      <ClientNavbar />
      <div className={`flex-1 transition-all duration-300 ${
        isSidebarMinimized ? 'ml-20' : 'ml-80'
      }`}>
        <div className="container mx-auto p-4">
          <CompanyProfile company={company} />
        </div>
      </div>
    </div>
  );
};

export default CompanyProfilePage;