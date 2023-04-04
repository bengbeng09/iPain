//Guest Login

btn_guest_login.setOnClickListener(v -> {
	showProgressBar();

	//Rember to login as guest everytime if remember me is checked
	
	if(cb_remember.isChecked()){
		ContentValues values = new ContentValues();
		values.put("id", 1);
		values.put("username", "guest");
		values.put("password", "guest");

		DBHelper dbHelper = new DBHelper(getContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.replace("user_table", null, values);

		db.close();
	}

	//Loading Datas
	
	new Handler().postDelayed(() -> {
		// Start the ActivityLoading activity
		Intent intent = new Intent(getActivity(), GuestActivity.class);
		toast("Login as Guest");
		startActivity(intent);
		requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


		if (getActivity() != null) {
			getActivity().finish();
		}
		// Hide the progress bar
		new Handler().postDelayed(this::hideProgressBar, 500);

	}, 2000);

});