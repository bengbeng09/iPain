//Setup Dashboard

private void set_dashboard(){
	switch (ActivityNavigation.user_type){
	
		//If Clinic Therapist
		
		case "Clinic Therapist":
		
			//Get patient count from firebase database
			
			DatabaseReference patientsRef = databaseReference.child("users").child(ActivityNavigation.user_code).child("patients");
			patientsRef.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot snapshot) {
					int patient_count = (int) snapshot.getChildrenCount();
					tv_patients.setText(String.valueOf(patient_count));
				}

				@Override
				public void onCancelled(@NonNull DatabaseError error) {

				}
			});
			container_therapist.setVisibility(View.VISIBLE);
			container_patient.setVisibility(View.GONE);

			break;
			
		//If clinic patient
		case "Clinic Patient":
			databaseReference.child("users").child(ActivityNavigation.user_code).addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot snapshot) {
					int pending = 0, completed = 0;
					
					//Count the pending and completed task in firebase database
					
					for (DataSnapshot therapistSnapshot : snapshot.child("therapists").getChildren()) {
						for (DataSnapshot activitySnapshot : therapistSnapshot.child("activities").getChildren()) {
							String status = activitySnapshot.child("status").getValue(String.class);
							if(status != null){
								if (status.equalsIgnoreCase("incomplete")) {
									pending++;
								}else{
									completed++;
								}
							}
						}
					}
					tv_pending_task.setText(String.valueOf(pending));
					tv_completed_task.setText(String.valueOf(completed));
				}

				@Override
				public void onCancelled(@NonNull DatabaseError error) {
					// Handle error
				}
			});
			container_therapist.setVisibility(View.GONE);
			container_patient.setVisibility(View.VISIBLE);
			break;
	}
}