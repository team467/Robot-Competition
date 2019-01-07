package frc.robot.utilities;

import edu.wpi.first.wpilibj.Preferences;

public class PreferencesStorage {

  // Singleton instance
  private static PreferencesStorage instance = null;

  // Data storage object
  private Preferences data;

  /**
   * Returns the single instance of this class.
   *
   * @return
   */
  public static PreferencesStorage getInstance() {
    if (instance == null) {
      instance = new PreferencesStorage();
    }
    return instance;
  }

  // Private constructor for singleton
  private PreferencesStorage() {
    data = Preferences.getInstance();
  }

  /**
   * Returns the int at the given key. If this table does not have a value for that position, 
   * then the given backup value will be returned.
   *
   * @param key
   *            The key.
   * @param backup
   *            The value to return if none exists in the table
   * @return
   */
  public int getInt(String key, int backup) {
    return data.getInt(key, backup);
  }

  /**
   * Returns the double at the given key. If this table does not have a value for that position, 
   * then the given backup value will be returned.
   *
   * @param key
   *            The key.
   * @param backup
   *            The value to return if none exists in the table
   * @return
   */
  public double getDouble(String key, double backup) {
    return data.getDouble(key, backup);
  }

  /**
   * Returns the boolean at the given key. If this table does not have a value for that position, 
   * then the given backup value will be returned.
   *
   * @param key
   *            The key.
   * @param backup
   *            The value to return if none exists in the table
   * @return
   */
  public boolean getBoolean(String key, boolean backup) {
    return data.getBoolean(key, backup);
  }

  /**
   * Returns the string at the given key. If this table does not have a value for that position, 
   * then the given backup value will be returned.
   *
   * @param key
   *            The key.
   * @param backup
   *            The value to return if none exists in the table
   * @return
   */
  public String getString(String key, String backup) {
    return data.getString(key, backup);
  }

  /**
   * Returns the float at the given key. If this table does not have a value for that position, 
   * then the given backup value will be returned.
   *
   * @param key
   *            The key.
   * @param backup
   *            The value to return if none exists in the table
   * @return
   */
  public float getFloat(String key, float backup) {
    return data.getFloat(key, backup);
  }

  /**
   * Returns the long at the given key. If this table does not have a value for that position, 
   * then the given backup value will be returned.
   *
   * @param key
   *            The key.
   * @param backup
   *            The value to return if none exists in the table
   * @return
   */
  public long getLong(String key, long backup) {
    return data.getLong(key, backup);
  }

  /**
   * Put the given int in the preferences table with the given key.
   *
   * @param key
   *            The key
   * @param value
   *            The value to put
   */
  public void putInt(String key, int value) {
    data.putInt(key, value);
  }

  /**
   * Put the given double in the preferences table with the given key.
   *
   * @param key
   *            The key
   * @param value
   *            The value to put
   */
  public void putDouble(String key, double value) {
    data.putDouble(key, value);
  }

  /**
   * Put the given boolean in the preferences table with the given key.
   *
   * @param key
   *            The key
   * @param value
   *            The value to put
   */
  public void putBoolean(String key, boolean value) {
    data.putBoolean(key, value);
  }

  /**
   * Put the given string in the preferences table with the given key.
   *
   * @param key
   *            The key
   * @param value
   *            The value to put
   */
  public void putString(String key, String value) {
    data.putString(key, value);
  }

  /**
   * Put the given float in the preferences table with the given key.
   *
   * @param key
   *            The key
   * @param value
   *            The value to put
   */
  public void putFloat(String key, float value) {
    data.putFloat(key, value);
  }

  /**
   * Put the given long in the preferences table with the given key.
   *
   * @param key
   *            The key
   * @param value
   *            The value to put
   */
  public void putLong(String key, long value) {
    data.putLong(key, value);
  }

  /**
   * Put an array of integers into the preferences table with the given key 
   * This doesn't actually store the values as an array, but rather individually 
   * matches a key to each one based on the given key and stores each integer as 
   * it's own value.
   *
   * @param key
   *            The key
   * @param values
   *            The integer array of values to store
   */
  public void putIntArray(String key, int[] values) {
    for (int i = 0; i < values.length; i++) {
      data.putInt(key + i, values[i]);
    }
    data.putInt(key + "length", values.length);
  }

  /**
   * Put an array of doubles into the preferences table with the given keys. This doesn't 
   * actually store the values as an array, but rather individually matches a key to each 
   * one based on the given key and stores each double as it's own value.
   *
   * @param key
   *            The key
   * @param values
   *            The double array of values to store
   */
  public void putDoubleArray(String key, double[] values) {
    for (int i = 0; i < values.length; i++) {
      data.putDouble(key + i, values[i]);
    }
    data.putInt(key + "length", values.length);
  }

  /**
   * Read an array of integers at the given key.
   *
   * @param key
   *            The key
   * @param backup
   *            A backup array to use if no value can be found
   * @return
   */
  public int[] getIntArray(String key, int[] backup) {
    int[] values = new int[data.getInt(key + "length", 0)];
    for (int i = 0; i < values.length; i++) {
      values[i] = data.getInt(key + i, backup[i]);
    }
    return values;
  }

  /**
   * Read an array of doubles at the given key.
   *
   * @param key
   *            The key
   * @param backup
   *            A backup array to use if no value can be found
   * @return
   */
  public double[] getDoubleArray(String key, double[] backup, int backupLength) {
    double[] values = new double[data.getInt(key + "length", backupLength)];
    for (int i = 0; i < values.length; i++) {
      values[i] = data.getDouble(key + i, backup[i]);
    }
    return values;
  }

  /**
   * Clear all the preferences (use with caution).
   */
  public void clear() {
    String key = "";
    for (int i = 0; i < data.getKeys().size(); i++) {
      key = (String) data.getKeys().elementAt(i);
      data.remove(key);
    }
  }
}
