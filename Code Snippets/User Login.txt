btn_login.setOnClickListener(view1 -> {

	//Get Username and Password
	final String username = et_username.getText().toString();
	final String password = et_password.getText().toString();

	// Check if the fields are not empty
	if(LoginActivity.isNotEmpty(new String[]{username, password})){

		// Check if the device is connected to internet
		if(getContext() != null & !LoginActivity.isNetworkConnected(getContext())){
			Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
			return;
		}

		// Access the Firebase database and look for the entered username
		databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {

				boolean usernameExists = false;
				User user = new User();

				// Loop through the users and look for the username
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
					// Check if the entered password is correct
					if(user.getPassword() != null && user.getPassword().equals(password)){

						// Store the user data in an array and pass it to the next activity
						String[][] data = {
								{"username", username},
								{"user_type", user.getUserType()},
								{"first_name", user.getFirstName()},
								{"last_name", user.getLastName()},
								{"user_code", user.getUserCode()},
								{"email", user.getEmail()}
						};

						// Save login info if remember me is checked
						if(cb_remember.isChecked()){
							ContentValues values = new ContentValues();
							values.put("id", 1);
							values.put("username", username);
							values.put("password", password);

							DBHelper dbHelper = new DBHelper(getContext());
							SQLiteDatabase db = dbHelper.getWritableDatabase();

							db.replace("user_table", null, values);

							db.close();
						}
						
						// Loading Datas
						showProgressBar();
						new Handler().postDelayed(() -> {
							// Start the ActivityLoading activity
							Intent intent = new Intent(getActivity(), ActivityNavigation.class);

							for (String[] pair : data) {
								intent.putExtra(pair[0], pair[1]);
							}

							toast("Login successfully");
							startActivity(intent);
							requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

							if (getActivity() != null) {
								getActivity().finish();
							}
							// Hide the progress bar
							new Handler().postDelayed(() -> {
								hideProgressBar();
							}, 500);

						}, 2000);


					}else{
						toast("Invalid Password");
					}

				}else{
					toast("Invalid Credentials");
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				toast("No internet connection");
			}
		});
	}else{
		toast("Do not leave any field blank");
	}

});