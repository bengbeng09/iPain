//Therapist, Add Patient

btn_confirm.setOnClickListener(v -> {
	Bundle bundle = new Bundle();

	//Check if patient name is filled in
	
	if(LoginActivity.isNotEmpty(new String[]{et_patient_name.getText().toString()})){
		String input = et_patient_name.getText().toString();
		String userCode;

		if (input.contains("(") && input.contains(")")) {
			userCode = input.substring(input.indexOf("(") + 1, input.indexOf(")"));

			//Check if user code is included and user exist in database
			
			if (!userCode.isEmpty()) {
				databaseReference.child("users").child(userCode)
						.addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
								if (dataSnapshot.exists()) {
									String user_code = dataSnapshot.getKey();

									//User exists
									
									if (user_code != null){
										DataSnapshot userSnapshot = dataSnapshot.child(user_code);
										String username = userSnapshot.child("username").getValue(String.class);

										//Send back the patient data to be added
										
										bundle.putString("user_code",user_code);
										bundle.putString("username",username);
										getParentFragmentManager().setFragmentResult("request_patient", bundle);
										getParentFragmentManager().beginTransaction().remove(TDialogAddPatient.this).commit();
									}else{
										Toast.makeText(getActivity(), "Unknown Error", Toast.LENGTH_SHORT).show();
									}
								} else {
									Toast.makeText(getActivity(), "Patient not found", Toast.LENGTH_SHORT).show();
								}
							}

							@Override
							public void onCancelled(@NonNull DatabaseError databaseError) {
								// Handle error here
							}
						});
			} else {
				Toast.makeText(getActivity(), "Invalid patient Name/ID", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getActivity(), "Invalid patient Name/ID", Toast.LENGTH_SHORT).show();
		}
	}else{
		Toast.makeText(getActivity(), "Enter patient Name/ID",
				Toast.LENGTH_SHORT).show();
	}
});