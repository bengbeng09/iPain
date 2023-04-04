//Patient, Opening an activity

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.p_task);

	findView();


	Bundle extras = getIntent().getExtras();

	//Set the activity infos
	
	if (extras != null) {
		activity_name = extras.getString("name");
		status = extras.getString("status");
		tv_name.setText(activity_name.toUpperCase());
		tv_repeat.setText(extras.getString("repetition"));
		tv_hold.setText(extras.getString("hold"));
		tv_complete.setText(extras.getString("complete"));
		tv_link.setText(extras.getString("link"));
		tv_notes.setText(extras.getString("note"));
		therapist_code = extras.getString("therapist");
	}

	
	//Get the activity image from database if it exists
	
	FirebaseStorage storage = FirebaseStorage.getInstance();
	StorageReference storageRef = storage.getReference();
	StorageReference imagesRef = storageRef.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).child("activities").child(activity_name).child("activity_image/image.jpg");

	imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
		// use the uri to load the image into an ImageView or to display it in some other way
		Glide.with(getApplicationContext()).load(uri).into(image);
	}).addOnFailureListener(new OnFailureListener() {
		@Override
		public void onFailure(@NonNull Exception e) {
			// handle any errors that occur while trying to retrieve the image
		   Toast.makeText(getApplicationContext(), "Error retrieving image, no internet connection", Toast.LENGTH_SHORT).show();
		}
	});

	//if the activity is already finished, remove the finish button
	
	if(status.equalsIgnoreCase("complete")){
		finish.setVisibility(View.GONE);
	}

	//Set up back button
	
	back.setOnClickListener(v -> {
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	});

	//Set up finish button
	
	finish.setOnClickListener(v ->{

		if(getApplicationContext() != null & !LoginActivity.isNetworkConnected(getApplicationContext())){
			Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure?")
				.setPositiveButton("Yes", (dialog, which) -> {
					DatabaseReference activity = databaseReference.child("users").child(ActivityNavigation.user_code).child("therapists").child(therapist_code).child("activities").child(activity_name);
					activity.child("status").setValue("complete");
					Toast.makeText(this, "Task Completed", Toast.LENGTH_SHORT).show();
					setResult(RESULT_OK);
					finish();
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
				})
				.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
		AlertDialog alertDialog = builder.create();
		alertDialog.show();

	});
	
	//Set up copy link on clicking the link

	card_link.setOnClickListener(v -> {
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("label", tv_link.getText().toString());
		clipboard.setPrimaryClip(clip);
		Toast.makeText(getApplicationContext(), "Link copied to clipboard", Toast.LENGTH_SHORT).show();
	});
}