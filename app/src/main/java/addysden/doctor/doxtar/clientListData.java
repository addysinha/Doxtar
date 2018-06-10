package addysden.doctor.doxtar;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Abhyudaya sinha on 3/30/2016.
 */
public class clientListData implements Parcelable {

    private String clientName;
    private String clientSpeciality;
    private String clientDescription;
    private String clientPhotoPath;
    private Bitmap clientPhotoBitmap;
    private String clientRefNumber;
    private String clientRating;
    private String clientView;
    private String clientType;

    public String getClientRefNumber() {
        return clientRefNumber;
    }

    public void setClientRefNumber(String clientRefNumber) {
        this.clientRefNumber = clientRefNumber;
    }

    clientListData() {

    }

    clientListData(Parcel destParcel) {
        clientName = destParcel.readString();
        clientDescription = destParcel.readString();
        clientSpeciality = destParcel.readString();
        clientPhotoPath = destParcel.readString();
        clientRefNumber = destParcel.readString();
        clientRating = destParcel.readString();
        clientView = destParcel.readString();
        clientType = destParcel.readString();
    }

    public String getClientRating() {
        return clientRating;
    }

    public void setClientRating(String clientRating) {
        this.clientRating = clientRating;
    }

    public String getClientView() {
        return clientView;
    }

    public void setClientView(String clientView) {
        this.clientView = clientView;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientSpeciality() {
        return clientSpeciality;
    }

    public void setClientSpeciality(String clientSpeciality) {
        this.clientSpeciality = clientSpeciality;
    }

    public String getClientDescription() {
        return clientDescription;
    }

    public void setClientDescription(String clientDescription) {
        this.clientDescription = clientDescription;
    }

    public String getClientPhotoPath() {
        return clientPhotoPath;
    }

    public void setClientPhotoPath(String clientPhotoPath) {
        this.clientPhotoPath = clientPhotoPath;
    }

    public Bitmap getClientPhotoBitmap() {
        return clientPhotoBitmap;
    }

    public void setClientPhotoBitmap(Bitmap clientPhotoBitmap) {
        this.clientPhotoBitmap = clientPhotoBitmap;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(clientName);
        dest.writeString(clientDescription);
        dest.writeString(clientSpeciality);
        dest.writeString(clientPhotoPath);
        dest.writeString(clientRefNumber);
        dest.writeString(clientRating);
        dest.writeString(clientView);
        dest.writeString(clientType);
        clientPhotoBitmap.writeToParcel(dest, flags);
    }

    public static final Creator<clientListData> CREATOR =
            new Creator<clientListData>() {

                @Override
                public clientListData createFromParcel(Parcel source) {
                    return new clientListData(source);
                }

                @Override
                public clientListData[] newArray(int size) {
                    return new clientListData[size];
                }
            };
}
