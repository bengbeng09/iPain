//Patient, Therapy session

//Get Activities

private void set_card(){
	tv_completed.setVisibility(View.GONE);
	tv_pending.setVisibility(View.GONE);
	container_incomplete.removeAllViews();
	container_completed.removeAllViews();
	
	//Get all the given activities of the clicked therapist
	
	DatabaseReference activity = databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").child(user_code).child("activities");
	activity.addListenerForSingleValueEvent(new ValueEventListener() {
		@Override
		public void onDataChange(@NonNull DataSnapshot snapshot) {
			for (DataSnapshot ds : snapshot.getChildren()) {
				String activity_name = ds.getKey();
				String repetition = ds.child("repetition").getValue(String.class);
				String hold = ds.child("hold").getValue(String.class);
				String complete = ds.child("complete").getValue(String.class);
				String link = ds.child("link").getValue(String.class);
				String note = ds.child("note").getValue(String.class);
				String status = ds.child("status").getValue(String.class);

				if(activity_name != null && status != null){
					activity.child(activity_name).addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(@NonNull DataSnapshot snapshot) {
							add_card(activity_name, repetition, hold, complete, link, note, status, user_code);
						}
						@Override
						public void onCancelled(@NonNull DatabaseError error) {
							// Handle error here
						}
					});
				}

			}

		}

		@Override
		public void onCancelled(@NonNull DatabaseError error) {

		}
	});
}

//Set Activities

private void add_card(String name, String repetition, String hold, String complete, String link, String note, String status, String therapist_code){
	@SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.layout_card_big, null);

	TextView nameView = view.findViewById(R.id.activity_name);
	CardView button = view.findViewById(R.id.btn_card);
	String cardDetails = name.toUpperCase();
	nameView.setText(cardDetails);

	//Set up activity on click
	
	button.setOnClickListener(v -> {
		Intent intent = new Intent(PTherapistSessionActivity.this, PTasksActivity.class);
		intent.putExtra("name", name);
		intent.putExtra("repetition", repetition);
		intent.putExtra("hold", hold);
		intent.putExtra("complete", complete);
		intent.putExtra("link", link);
		intent.putExtra("note", note);
		intent.putExtra("status", status);
		intent.putExtra("therapist", therapist_code);
		startActivityForResult(intent, REQUEST_CODE_SECOND_ACTIVITY);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	});

	//Check if status is incomplete
	
	if(status.equalsIgnoreCase("incomplete")){
		if(tv_pending.getVisibility() == View.GONE){
			tv_pending.setVisibility(View.VISIBLE);
		}
		container_incomplete.addView(view);
	}else{
		if(tv_completed.getVisibility() == View.GONE){
			tv_completed.setVisibility(View.VISIBLE);
		}
		int newColor = getResources().getColor(R.color.teal_200);
		ColorStateList colorStateList = ColorStateList.valueOf(newColor);
		button.setBackgroundTintList(colorStateList);
		container_completed.addView(view);
	}
	view.setTranslationY(-100f);
	view.setAlpha(0f);
	view.animate().translationYBy(100f).alpha(1f).setDuration(500);
}