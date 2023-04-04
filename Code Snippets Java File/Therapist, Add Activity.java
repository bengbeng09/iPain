//Therapist, Add Activity to Patient

btn_confirm.setOnClickListener(v -> {
	Bundle bundle = new Bundle();
	
	//Get all the entered data in fields
	
	final String activity_name = et_activity.getText().toString();
	final String activity_repetition = et_repetition.getText().toString();
	final String activity_hold = et_hold.getText().toString();
	final String activity_complete = et_complete.getText().toString();
	final String activity_link = et_link.getText().toString();
	final String activity_note = et_therapist.getText().toString();

	//Check if all fields is filled in
	
	if(LoginActivity.isNotEmpty(new String[]{activity_name, activity_repetition, activity_hold, activity_complete, activity_link, activity_note})){

		bundle.putString("a_name", activity_name);
		bundle.putString("a_repetition", activity_repetition);
		bundle.putString("a_hold", activity_hold);
		bundle.putString("a_complete", activity_complete);
		bundle.putString("a_link", activity_link);
		bundle.putString("a_note", activity_note);

		//Get the image if theres an image attached
		
		if(bitmap != null){
			bundle.putParcelable("a_image", bitmap);
		}
		
		getParentFragmentManager().setFragmentResult("request_activity", bundle);
		getParentFragmentManager().beginTransaction().remove(PDialogAddActivity.this).commit();

	}else{
		Toast.makeText(getActivity(), "Fill up all fields",
				Toast.LENGTH_LONG).show();
	}
});