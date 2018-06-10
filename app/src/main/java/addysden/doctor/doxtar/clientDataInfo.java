package addysden.doctor.doxtar;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Abhyudaya sinha on 4/16/2016.
 */
public class clientDataInfo implements Parcelable {

    private String mClientName, mClientSpeciality, mClientAddress, mClientPhone1, mClientPhone2,
                mClientEmail, mClientDescription, mClientView, mClientRating, mClientType;

    clientDataInfo() {
    }

    clientDataInfo(Parcel destParcel) {
        mClientName = destParcel.readString();
        mClientSpeciality = destParcel.readString();
        mClientAddress = destParcel.readString();
        mClientPhone1 = destParcel.readString();
        mClientPhone2 = destParcel.readString();
        mClientEmail = destParcel.readString();
        mClientDescription = destParcel.readString();
        mClientView = destParcel.readString();
        mClientRating = destParcel.readString();
        mClientType = destParcel.readString();
    }

    public String getmClientType() {
        return mClientType;
    }

    public void setmClientType(String mClientType) {
        this.mClientType = mClientType;
    }

    public String getmClientView() {
        return mClientView;
    }

    public void setmClientView(String mClientView) {
        this.mClientView = mClientView;
    }

    public String getmClientRating() {
        return mClientRating;
    }

    public void setmClientRating(String mClientRating) {
        this.mClientRating = mClientRating;
    }

    public String getmClientName() {
        return mClientName;
    }

    public void setmClientName(String mClientName) {
        this.mClientName = mClientName;
    }

    public String getmClientSpeciality() {
        return mClientSpeciality;
    }

    public void setmClientSpeciality(String mClientSpeciality) {
        this.mClientSpeciality = mClientSpeciality;
    }

    public String getmClientAddress() {
        return mClientAddress;
    }

    public void setmClientAddress(String mClientAddress) {
        this.mClientAddress = mClientAddress;
    }

    public String getmClientPhone1() {
        return mClientPhone1;
    }

    public void setmClientPhone1(String mClientPhone1) {
        this.mClientPhone1 = mClientPhone1;
    }

    public String getmClientPhone2() {
        return mClientPhone2;
    }

    public void setmClientPhone2(String mClientPhone2) {
        this.mClientPhone2 = mClientPhone2;
    }

    public String getmClientEmail() {
        return mClientEmail;
    }

    public void setmClientEmail(String mClientEmail) {
        this.mClientEmail = mClientEmail;
    }

    public String getmClientDescription() {
        return mClientDescription;
    }

    public void setmClientDescription(String mClientDescription) {
        this.mClientDescription = mClientDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mClientName);
        dest.writeString(mClientSpeciality);
        dest.writeString(mClientAddress);
        dest.writeString(mClientPhone1);
        dest.writeString(mClientPhone2);
        dest.writeString(mClientEmail);
        dest.writeString(mClientDescription);
        dest.writeString(mClientView);
        dest.writeString(mClientRating);
        dest.writeString(mClientType);
    }

    public static final Creator<clientDataInfo> CREATOR =
            new Creator<clientDataInfo>() {

                @Override
                public clientDataInfo createFromParcel(Parcel source) {
                    return new clientDataInfo(source);
                }

                @Override
                public clientDataInfo[] newArray(int size) {
                    return new clientDataInfo[size];
                }
            };
}
