//Change Password Function

btn_submit.setOnClickListener(v -> {

	// Get the entered current password, new password, and re-typed new password.
	
	final String current_password = et_current_password.getText().toString();
	final String new_password = et_new_password.getText().toString();
	final String re_new_password = et_retype_new_password.getText().toString();

	// Check if all fields are filled in.
	
	if(LoginActivity.isNotEmpty(new String[]{current_password, new_password, re_new_password})){

		// Check if the new password matches the re-typed new password.
		
		if(new_password.equals(re_new_password)){

			// Check if the new password is at least 7 characters long.
			
			if(new_password.length() < 7){
				Toast.makeText(getContext(), "New password is too short", Toast.LENGTH_SHORT).show();
				return;
			}

			DatabaseReference userRef = databaseReference.child("users").child(ActivityNavigation.user_code);
			userRef.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot snapshot) {
					String username_check = snapshot.child("username").getValue(String.class);

					if (username_check != null && username_check.equals(ActivityNavigation.username)) {
						String current_password_check = snapshot.child("password").getValue(String.class);

						// Check if the entered current password matches the current password in the database.
						
						if (current_password.equals(current_password_check)) {

							// Update the password in the database.
							
							userRef.child("password").setValue(new_password);
							Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
							getParentFragmentManager().beginTransaction().remove(DialogChangePass.this).commit();

						} else {
							Toast.makeText(getContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getContext(), "System Error", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onCancelled(@NonNull DatabaseError error) {
					// Handle error
				}
			});
		}else{
			Toast.makeText(getContext(), "New password does not match", Toast.LENGTH_SHORT).show();
		}
	}else{
		Toast.makeText(getContext(), "Do not leave any field blank", Toast.LENGTH_SHORT).show();
	}

});