package addysden.doctor.doxtar;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class MyClientListRecyclerViewAdapter extends ArrayAdapter {

    private List<clientListData> clientListDataList;
    private int resource;
    private LayoutInflater inflater;

    public MyClientListRecyclerViewAdapter(Context context, int resource, List<clientListData> objects) {
        super(context, resource, objects);

        clientListDataList = objects;
        this.resource = resource;
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_clientlist, null);
        }

        ImageView clientListImageView = (ImageView) convertView.findViewById(R.id.client_list_image_view);
        TextView clientNameTextView = (TextView) convertView.findViewById(R.id.Client_Name_TextView);
        TextView clientDescriptionTextView = (TextView) convertView.findViewById(R.id.Client_Description_TextView);
        TextView clientSpecialityTextView = (TextView) convertView.findViewById(R.id.Client_Speciality_TextView);

        TextView clientRatingTextView = (TextView) convertView.findViewById(R.id.client_list_rating_textview);
        RatingBar clientRatingRatingBar = (RatingBar) convertView.findViewById(R.id.client_list_ratingBar);
        TextView clientViewTextView = (TextView) convertView.findViewById(R.id.client_list_views_textview);

        TextView clientTypeTextView = (TextView) convertView.findViewById(R.id.client_list_client_type_text_view);
        ImageView clientTypeImageView = (ImageView) convertView.findViewById(R.id.client_list_client_type_image_view);

        clientNameTextView.setText(clientListDataList.get(position).getClientName());
        clientDescriptionTextView.setText(clientListDataList.get(position).getClientDescription());
        clientSpecialityTextView.setText(clientListDataList.get(position).getClientSpeciality());
        clientListImageView.setImageBitmap(clientListDataList.get(position).getClientPhotoBitmap());
        clientListImageView.setTag(clientListDataList.get(position).getClientRefNumber());

        clientRatingTextView.setText("(" + clientListDataList.get(position).getClientRating() + "/5.0)");
        clientRatingRatingBar.setRating(Float.parseFloat(clientListDataList.get(position).getClientRating()));
        clientViewTextView.setText(clientListDataList.get(position).getClientView());
        clientTypeTextView.setText(clientListDataList.get(position).getClientType());


        if(clientListDataList.get(position).getClientType().equals("Doctor")) {
            DrawableCompat.setTint(clientTypeImageView.getDrawable(), ContextCompat.getColor(getContext(), R.color.colorPurple));
            clientTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colorPurple));
        } else if (clientListDataList.get(position).getClientType().equals("Hospital")) {
            DrawableCompat.setTint(clientTypeImageView.getDrawable(), ContextCompat.getColor(getContext(), R.color.colorPink));
            clientTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colorPink));
        } else if (clientListDataList.get(position).getClientType().equals("Chemist")) {
            DrawableCompat.setTint(clientTypeImageView.getDrawable(), ContextCompat.getColor(getContext(), R.color.colorGreen));
            clientTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colorGreen));
        } else if (clientListDataList.get(position).getClientType().equals("Pathology")) {
            DrawableCompat.setTint(clientTypeImageView.getDrawable(), ContextCompat.getColor(getContext(), R.color.colorOrange));
            clientTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colorOrange));
        }

        return convertView;
    }

}
