import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import userService from "../../services/userService";

function CompanyRegister() {
  const [userData, setUserData] = useState({
    email: "",
    password: "",
    confirmPassword: "",
    name: "",
    role: "COMPANY", // Fixed role for company registration
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUserData({ ...userData, [name]: value });
  };

  const validateForm = () => {
    // Check if passwords match
    if (userData.password !== userData.confirmPassword) {
      setError("Passwords do not match");
      return false;
    }

    // Check if password is strong enough (at least 6 characters)
    if (userData.password.length < 6) {
      setError("Password must be at least 6 characters long");
      return false;
    }

    // Check if all required fields are filled
    if (!userData.email || !userData.password || !userData.name) {
      setError("All fields are required");
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validate form
    if (!validateForm()) {
      return;
    }

    setLoading(true);
    setError("");

    try {
      // Remove confirmPassword before sending to API
      const { confirmPassword, ...registrationData } = userData;

      // Register user
      await userService.register(registrationData);

      // Redirect to login page after successful registration
      navigate("/company/login");
    } catch (err) {
      if (err.response && err.response.status === 400) {
        setError("Email already exists. Please use a different email.");
      } else {
        setError("Registration failed. Please try again.");
        console.error("Registration error:", err);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="relative flex flex-col justify-center h-screen overflow-hidden">
        <div className="w-full p-6 m-auto bg-white rounded-md shadow-md ring-2 ring-green-400 lg:max-w-lg">
          <h1 className="text-3xl font-semibold text-center text-gray-700">
            Company Registration
          </h1>
          {error && (
            <div className="p-3 my-2 text-sm text-red-700 bg-red-100 rounded-lg">
              {error}
            </div>
          )}
          <form className="space-y-4" onSubmit={handleSubmit}>
            <div>
              <label className="label">
                <span className="text-base label-text">Company Name</span>
              </label>
              <input
                type="text"
                name="name"
                placeholder="Company Name"
                className="w-full input input-bordered"
                value={userData.name}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <label className="label">
                <span className="text-base label-text">Email</span>
              </label>
              <input
                type="email"
                name="email"
                placeholder="Email Address"
                className="w-full input input-bordered"
                value={userData.email}
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
                value={userData.password}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <label className="label">
                <span className="text-base label-text">Confirm Password</span>
              </label>
              <input
                type="password"
                name="confirmPassword"
                placeholder="Confirm Password"
                className="w-full input input-bordered"
                value={userData.confirmPassword}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <button
                type="submit"
                className="btn-success btn btn-block"
                disabled={loading}
              >
                {loading ? "Registering..." : "Register"}
              </button>
            </div>
            <div className="text-center">
              <p>
                Already have an account?{" "}
                <Link to="/company/login" className="text-blue-600 hover:underline">
                  Login
                </Link>
              </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default CompanyRegister;