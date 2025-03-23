import { useState, useEffect } from 'react';
import userService from '../../services/userService';
import settingsService from '../../services/settingsService';
import { toast } from 'react-hot-toast';

const ProfileSettings = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    profilePicture: ''
  });
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadUserProfile = async () => {
      setLoading(true);
      setError(null);
      try {
        const currentUser = userService.getCurrentUser();
        if (currentUser) {
          const userData = await userService.getUserById(currentUser.id);
          setFormData({
            name: userData.name || '',
            email: userData.email || '',
            phone: userData.phone || '',
            profilePicture: userData.profileImage || ''
          });
        }
      } catch (err) {
        console.error('Error loading profile:', err);
        setError('Failed to load profile data. Please try again.');
        toast.error('Failed to load profile data');
      } finally {
        setLoading(false);
      }
    };

    loadUserProfile();
  }, []);

  const handleSaveChanges = async () => {
    setSaving(true);
    setError(null);
    try {
      const currentUser = userService.getCurrentUser();
      if (currentUser) {
        // Prepare data for API
        const profileData = {
          name: formData.name,
          email: formData.email,
          phone: formData.phone,
          profileImage: formData.profilePicture
        };

        await settingsService.updateProfile(currentUser.id, profileData);
        toast.success('Profile updated successfully');
      }
    } catch (err) {
      console.error('Error saving profile:', err);
      setError('Failed to save profile changes. Please try again.');
      toast.error('Failed to save profile changes');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold text-gray-900 dark:text-white">Profile Settings</h2>

      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 ">Name</label>
          <input
            type="text"
            className="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white shadow-sm focus:border-yellow-500 focus:ring-yellow-500"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">Email</label>
          <input
            type="email"
            className="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white shadow-sm focus:border-yellow-500 focus:ring-yellow-500"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">Phone</label>
          <input
            type="tel"
            className="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white shadow-sm focus:border-yellow-500 focus:ring-yellow-500"
            value={formData.phone}
            onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">Profile Picture URL</label>
          <input
            type="url"
            className="mt-1 block w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white shadow-sm focus:border-yellow-500 focus:ring-yellow-500"
            value={formData.profilePicture}
            onChange={(e) => setFormData({ ...formData, profilePicture: e.target.value })}
          />
        </div>

        <button
          type="button"
          onClick={handleSaveChanges}
          disabled={loading || saving}
          className="mt-4 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-yellow-600 hover:bg-yellow-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500 dark:ring-offset-gray-800"
        >
          {saving ? 'Saving...' : 'Save Changes'}
        </button>

        {error && (
          <div className="mt-2 text-sm text-red-600">
            {error}
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfileSettings;