import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import userService from "../../services/userService";

function ClientLogin() {
  const [credentials, setCredentials] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCredentials({ ...credentials, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      // First check if user exists and get their role
      const userResponse = await userService.getUserByEmail(credentials.email);

      // Check if user exists and has correct role
      if (!userResponse) {
        setError("Invalid email or password. Please try again.");
        return;
      }

      if (userResponse.role !== "CLIENT") {
        setError(
          <div className="space-y-2">
            <p>This login page is for clients only.</p>
            <p>If you are a company, please
              <Link to="/company/login" className="text-blue-600 hover:underline mx-1">
                login here
              </Link>
              instead.
            </p>
          </div>
        );
        return;
      }

      // Proceed with login only if role is correct
      const response = await userService.login(credentials.email, credentials.password);
      navigate("/client/home");

    } catch (err) {
      if (err.response?.status === 404) {
        setError("No account found with this email address.");
      } else {
        setError("Invalid email or password. Please try again.");
      }
      console.error("Login error:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="relative flex flex-col justify-center h-screen overflow-hidden">
        <div className="w-full p-6 m-auto bg-white rounded-md shadow-md ring-2 ring-yellow-400 lg:max-w-lg">
          <h1 className="text-3xl font-semibold text-center text-gray-700">
            Client Login
          </h1>
          {error && (
            <div className="p-3 my-2 text-sm text-red-700 bg-red-100 rounded-lg">
              {error}
            </div>
          )}
          <form className="space-y-4" onSubmit={handleSubmit}>
            <div>
              <label className="label">
                <span className="text-base label-text">Email</span>
              </label>
              <input
                type="text"
                name="email"
                placeholder="Email Address"
                className="w-full input input-bordered"
                value={credentials.email}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <label className="label">
                <span className="text-base label-text">Password</span>
              </label>
              <input
                type="password"
                name="password"
                placeholder="Enter Password"
                className="w-full input input-bordered"
                value={credentials.password}
                onChange={handleChange}
                required
              />
            </div>
            <a href="#" className="text-xs text-gray-600 hover:underline hover:text-blue-600">
              Forget Password?
            </a>
            <div>
              <button
                type="submit"
                className="btn-neutral btn btn-block"
                disabled={loading}
              >
                {loading ? "Logging in..." : "Login"}
              </button>
            </div>
            <div className="text-center mt-4">
              <Link to="/" className="text-blue-600 hover:underline">
                Back to Login Selection
              </Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default ClientLogin;