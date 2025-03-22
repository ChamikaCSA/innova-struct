import { Link } from 'react-router-dom';

const ChooseLogin = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
      <h1 className="text-3xl font-bold mb-8">Choose Login Type</h1>
      <div className="space-y-4">
        <Link
          to="/client/login"
          className="block px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
        >
          Client Login
        </Link>
        <Link
          to="/company/login"
          className="block px-6 py-3 bg-green-500 text-white rounded-lg hover:bg-green-600"
        >
          Company Login
        </Link>
      </div>
      <div className="mt-6">
        <p className="text-gray-600">
          Don't have an account?{" "}
          <Link to="/register" className="text-blue-600 hover:underline">
            Register Now
          </Link>
        </p>
      </div>
    </div>
  );
};

export default ChooseLogin;