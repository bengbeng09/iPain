//Register Account

btn_register.setOnClickListener(view1 -> {

	//Get all entered fields
	final String user_type = spinner_user_type.getSelectedItem().toString();
	final String last_name = et_last_name.getText().toString();
	final String first_name = et_first_name.getText().toString();
	final String middle_name = et_middle_name.getText().toString();
	final String clinic_name = et_clinic_name.getText().toString();
	final String email = et_email.getText().toString();
	final String username = et_username.getText().toString();
	final String password = et_password.getText().toString();
	final String reenter_password = et_reenter_password.getText().toString();

	//Check if all fields is filled in
	
	if(LoginActivity.isNotEmpty(new String[]{user_type,last_name, first_name, middle_name, email, username, password, reenter_password})){

		//Check if theres an internet connection
		if(getContext() != null & !LoginActivity.isNetworkConnected(getContext())){
			Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
			return;
		}

		// Get reference of firebase database
		
		databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {

				//Check if username and email already exist
				
				for (DataSnapshot userSnapshot : snapshot.getChildren()) {
					String username_check = userSnapshot.child("username").getValue(String.class);
					String email_check = userSnapshot.child("email").getValue(String.class);

					if (username_check != null && username_check.equalsIgnoreCase(username)) {
						toast("Username already exists");
						return;
					}

					if (email_check != null && email_check.equalsIgnoreCase(email)) {
						toast("Email already exists");
						return;
					}
				}

				//Check if username is valid
				
				if (!username.matches("^[a-zA-Z0-9](_(?!([._]))|\\.(?!([_.]))|[a-zA-Z0-9]){8,18}[a-zA-Z0-9]$")) {
					if (username.length() < 8 || username.length() > 18) {
						toast("Username must be between 8 and 18 characters long");
					} else if (!username.matches("[a-zA-Z0-9._]+")) {
						toast("Username can only contain alphanumeric characters, dots, or underscores");
					} else if (username.startsWith(".") || username.startsWith("_") || username.endsWith(".") || username.endsWith("_")) {
						toast("Username cannot start or end with a dot or underscore");
					} else {
						toast("Username must contain at least one alphanumeric character");
						}
					return;
				}

				//Check password is equal to re-entered password
				
				if(!password.equals(reenter_password)){
					toast("Password does not match");
					return;
				}

				//Check if password is less than 7
				
				if(password.length() < 7){
					toast("Password is too short");
					return;
				}

				//Generate six character random hex to be used as user id
				
				Random random = new Random();
				String hex = Integer.toHexString(random.nextInt(16777216)).toUpperCase();
				hex = String.format("%06x", Integer.parseInt(hex, 16));
				final String user_code = hex.toUpperCase();

				//Add the new user
				
				User user = new User();
				user.setUsername(username);
				user.setUser_type(user_type);
				user.setLast_name(last_name);
				user.setFirst_name(first_name);
				user.setMiddle_name(middle_name);
				if(user_type.equals("Clinic Therapist")){
					user.setClinic_name(clinic_name);
				}
				user.setEmail(email);
				user.setPassword(password);

				databaseReference.child("users").child(user_code).setValue(user);

				toast("Registered Successfully");

				Fragment fragment= getParentFragmentManager().findFragmentByTag("login");

				//Return to login 
				
				if(fragment != null){
					getActivity().getSupportFragmentManager().beginTransaction()
							.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
							.replace(((ViewGroup) getView().getParent()).getId(), fragment)
							.remove(RegisterFragment.this)
							.commit();
				}

			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				toast("unknown database error");
			}
		});
	}else{
		toast("do not leave any field blank");
	}
});