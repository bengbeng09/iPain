//Forgot Password Function, Email OTP Verification

btn_send_email.setOnClickListener( v -> {

	// Get the entered email
	
	String email = et_email.getText().toString();

	// Check if email is filled in.
	
	if (LoginActivity.isNotEmpty(new String[]{email})) {

		// Check if theres an internet connection
		
		if(getContext() != null & !LoginActivity.isNetworkConnected(getContext())){
			Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
			return;
		}

		// Check if email is registered on existing users
		
		databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				boolean emailExist = false;
				for (DataSnapshot userSnapshot : snapshot.getChildren()) {
					String email_check = userSnapshot.child("email").getValue(String.class);
					user_code = userSnapshot.getKey();
					if (email_check != null && email_check.equals(email)) {
						emailExist = true;
						break;
					}
				}

				//Email exists
				
				if(emailExist){

					//Generate six digit code that will be sent to email
					Random random = new Random();
					String ResetCode = Integer.toHexString(random.nextInt(16777216)).toUpperCase();
					ResetCode = String.format("%06x", Integer.parseInt(ResetCode, 16));
					reset_code = ResetCode.toUpperCase();

					//Send the six digit code with a message using JavaMailAPI
					
					JavaMailAPI javaMailAPI = new JavaMailAPI(getActivity(), email, "Password Reset Request for iPAIN App", "Dear iPAIN User,\n" +
							"\n" +
							"We received a request to reset your iPain app password. To ensure your safety and privacy, remember to use a unique and strong password, avoid sharing personal info, and keep the app updated.\n" +
							"\n" +
							"Your reset code is "+ reset_code +" . If you didn't request this, contact support at 09574225472. Thank you for choosing iPAIN.\n" +
							"\n" +
							"Best regards,\n" +
							"iPAIN Team");
					javaMailAPI.execute();

					container_email.setVisibility(View.GONE);
					container_code.setVisibility(View.VISIBLE);
				}else{
					Toast.makeText(getContext(), "Entered email is not registered", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
			}
		});
	}else{
		Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
	}
});