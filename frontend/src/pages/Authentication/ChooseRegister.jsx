import { Link } from 'react-router-dom';

const ChooseRegister = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
      <h1 className="text-3xl font-bold mb-8">Choose Registration Type</h1>
      <div className="space-y-4">
        <Link
          to="/client/register"
          className="block px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
        >
          Client Registration
        </Link>
        <Link
          to="/company/register"
          className="block px-6 py-3 bg-green-500 text-white rounded-lg hover:bg-green-600"
        >
          Company Registration
        </Link>
      </div>
      <div className="mt-6">
        <p className="text-gray-600">
          Already have an account?{" "}
          <Link to="/" className="text-blue-600 hover:underline">
            Choose Login Type
          </Link>
        </p>
      </div>
    </div>
  );
};

export default ChooseRegister;