//Patient, Add Therapist

btn_submit.setOnClickListener(v -> {

	//Get the entered therapist code
	
	String therapist_code = et_therapist_code.getText().toString().toUpperCase();
	
	//Check therapist code is not empty
	
	if(LoginActivity.isNotEmpty(new String[]{et_therapist_code.getText().toString()})){

		//Check if theres an internet connection
		
		if(getContext() != null & !LoginActivity.isNetworkConnected(getContext())){
			Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
			return;
		}

		//Get Firebase Database reference and check if therapist code exist in users
		
		databaseReference.child("users").child(therapist_code).addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				
					if (dataSnapshot.exists()) {
						databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(@NonNull DataSnapshot snapshot) {
								if (!snapshot.exists()) {
									String user_type = dataSnapshot.child("user_type").getValue(String.class);

									//Check if entered therapist code is a therapist account
									
									if(user_type != null && user_type.equals("Clinic Therapist")){
									
										//Add the therapist to patients therapists
										
										databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).child("status").setValue("active");
										databaseReference.child("users").child(therapist_code).child("patients").child(ActivityNavigation.user_code).child("status").setValue("active");

										Animation slideOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);
										add_therapist.startAnimation(slideOutAnim);
										add_therapist.setVisibility(View.GONE);

										Animation slideInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
										therapists.startAnimation(slideInAnim);
										therapists.setVisibility(View.VISIBLE);

										set_card();

										et_therapist_code.setText(null);
										Toast.makeText(getActivity(), "Therapist added successfully", Toast.LENGTH_SHORT).show();

									}else{
										Toast.makeText(getActivity(), "Entered code is not a therapist", Toast.LENGTH_SHORT).show();
									}

								}else{
									Toast.makeText(getActivity(), "Therapist already added", Toast.LENGTH_SHORT).show();
								}
							}
							@Override
							public void onCancelled(@NonNull DatabaseError error) {

							}
						});

					} else {
						Toast.makeText(getActivity(), "Invalid therapist code", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
					// Handle error here
				}
			});
	}else{
		Toast.makeText(getActivity(), "Enter Therapist Code",
				Toast.LENGTH_SHORT).show();
	}
});