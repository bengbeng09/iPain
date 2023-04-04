//Settings

public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	// Inflate the layout for this fragment
	View view = inflater.inflate(R.layout.menu_settings, container, false);

	findView(view);
	
	//Set the user id, username, name and email
	
	tv_user_id.setText(ActivityNavigation.user_code);
	tv_name.setText(ActivityNavigation.firstname.toUpperCase() + " " + ActivityNavigation.lastname.toUpperCase());
	tv_username.setText(ActivityNavigation.username);
	tv_email.setText(ActivityNavigation.email);

	getParentFragmentManager().setFragmentResultListener("request_info", this, new FragmentResultListener() {
		@Override
		public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
			String user_type = bundle.getString("type");
			switch (user_type){
				case "Clinic Therapist":
					break;
				case "Clinic Patient":
					break;
			}
		}
	});

	//Set up logout button and clear remember if me
	logout.setOnClickListener(v -> {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Logout?")
				.setPositiveButton("Yes", (dialog, which) -> {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

					DBHelper dbHelper = new DBHelper(getActivity());
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					db.delete("user_table", null, null);
					db.close();
					
					startActivity(intent);
					requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
				})
				.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
		AlertDialog alertDialog = builder.create();
		alertDialog.show();

	});

	//Set up change password button
	change_password.setOnClickListener( v -> {
		DialogChangePass dialogChangePass = new DialogChangePass();
		dialogChangePass.setCancelable(false);
		dialogChangePass.show(getParentFragmentManager(), "change pass");
	});

	return view;
}