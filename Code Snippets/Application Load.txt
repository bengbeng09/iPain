//App Load

protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_loading);

	//Check if theres a saved login info
	
	String[] columns = {"username", "password"};
	String selection = "id = ?";
	String[] selectionArgs = {"1"};

	DBHelper dbHelper = new DBHelper(getApplicationContext());
	SQLiteDatabase db = dbHelper.getReadableDatabase();

	Cursor cursor = db.query("user_table", columns, selection, selectionArgs, null, null, null);

	String saved_username = "";
	String saved_password = "";
	boolean remembered = false;

	int usernameIndex = cursor.getColumnIndex("username");
	int passwordIndex = cursor.getColumnIndex("password");

	if (cursor.moveToFirst()) {
		if (usernameIndex >= 0) {
			saved_username = cursor.getString(usernameIndex);
		}
		if (passwordIndex >= 0) {
			saved_password = cursor.getString(passwordIndex);
		}
		remembered = true;
	}

	cursor.close();
	db.close();


	//If a login info is saved, Login using the saved info
	
	if(remembered){
		final String username = saved_username;
		final String password = saved_password;

		if(LoginActivity.isNotEmpty(new String[]{username, password})){
			if(username.equals("guest")){
				startDelayedActivity(GuestActivity.class, 2000);
			}else{
				databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot snapshot) {

						boolean usernameExists = false;
						LoginFragment.User user = new LoginFragment.User();

						for (DataSnapshot userSnapshot : snapshot.getChildren()) {
							String username_check = userSnapshot.child("username").getValue(String.class);

							if (username_check != null && username_check.equals(username)) {
								user.setUserCode(userSnapshot.getKey());
								user.setPassword(userSnapshot.child("password").getValue(String.class));
								user.setUserType(userSnapshot.child("user_type").getValue(String.class));
								user.setFirstName(userSnapshot.child("first_name").getValue(String.class));
								user.setLastName(userSnapshot.child("last_name").getValue(String.class));
								user.setEmail(userSnapshot.child("email").getValue(String.class));
								usernameExists = true;
								break;
							}
						}

						if(usernameExists){

							if(user.getPassword() != null && user.getPassword().equals(password)){

								String[][] data = {
										{"username", username},
										{"user_type", user.getUserType()},
										{"first_name", user.getFirstName()},
										{"last_name", user.getLastName()},
										{"user_code", user.getUserCode()},
										{"email", user.getEmail()}
								};

								new Handler().postDelayed(() -> {
									Intent intent = new Intent(ActivityLoading.this, ActivityNavigation.class);

									for (String[] pair : data) {
										intent.putExtra(pair[0], pair[1]);
									}

									startActivity(intent);
									finish();
								}, 2000);

							}
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError error) {

					}
				});
			}
		}else{
			Toast.makeText(this,"Saved account error", Toast.LENGTH_SHORT).show();
			startDelayedActivity(LoginActivity.class, 2000);
		}
	}else{
		startDelayedActivity(LoginActivity.class, 2000);
	}
}