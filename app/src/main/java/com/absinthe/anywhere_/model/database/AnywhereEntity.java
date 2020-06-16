package com.absinthe.anywhere_.model.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.absinthe.anywhere_.constants.AnywhereType;
import com.absinthe.anywhere_.constants.GlobalValues;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity(tableName = "anywhere_table")
public class AnywhereEntity implements Parcelable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    private String mId;

    @NonNull
    @ColumnInfo(name = "app_name")
    @SerializedName("app_name")
    private String mAppName;

    @NonNull
    @ColumnInfo(name = "param_1")
    @SerializedName("param_1")
    private String mParam1;

    @ColumnInfo(name = "param_2")
    @SerializedName("param_2")
    private String mParam2;

    @ColumnInfo(name = "param_3")
    @SerializedName("param_3")
    private String mParam3;

    @ColumnInfo(name = "description")
    @SerializedName("description")
    private String mDescription;

    @NonNull
    @ColumnInfo(name = "type")
    @SerializedName("type")
    private Integer mType;

    @ColumnInfo(name = "category")
    @SerializedName("category")
    private String mCategory;

    @NonNull
    @ColumnInfo(name = "time_stamp")
    @SerializedName("time_stamp")
    private String mTimeStamp;

    @NonNull
    @ColumnInfo(name = "color")
    @SerializedName("color")
    private Integer mColor;

    @ColumnInfo(name = "iconUri")
    @SerializedName("iconUri")
    private String mIconUri;

    public static AnywhereEntity Builder() {
        String time = System.currentTimeMillis() + "";
        return new AnywhereEntity(
                time,
                "",
                "",
                "",
                "",
                "",
                AnywhereType.Card.ACTIVITY,
                GlobalValues.INSTANCE.getCategory(),
                time,
                0,
                "");
    }

    public AnywhereEntity(@NonNull String id, @NonNull String appName, @NonNull String param1,
                          String param2, String param3, String description,
                          @NonNull Integer type, String category, @NonNull String timeStamp,
                          @NonNull Integer color, String iconUri) {
        mId = id;
        mAppName = appName;
        mParam1 = param1;
        mParam2 = param2;
        mParam3 = param3;
        mDescription = description;
        mType = type;
        mCategory = category;
        mTimeStamp = timeStamp;
        mColor = color;
        mIconUri = iconUri;

        if (param2 == null) {
            mParam2 = "";
        }

        if (param3 == null) {
            mParam3 = "";
        }

        if (description == null) {
            mDescription = "";
        }

        if (category == null) {
            mCategory = "";
        }

        if (iconUri == null) {
            mIconUri = "";
        }
    }

    public AnywhereEntity(AnywhereEntity ae) {
        mId = ae.getId();
        mAppName = ae.getAppName();
        mParam1 = ae.getParam1();
        mParam2 = ae.getParam2();
        mParam3 = ae.getParam3();
        mDescription = ae.getDescription();
        mType = ae.getType();
        mCategory = ae.getCategory();
        mTimeStamp = ae.getTimeStamp();
        mColor = ae.getColor();
        mIconUri = ae.getIconUri();
    }

    private AnywhereEntity(Parcel in) {
        mId = Objects.requireNonNull(in.readString());
        mAppName = Objects.requireNonNull(in.readString());
        mParam1 = Objects.requireNonNull(in.readString());
        mParam2 = in.readString();
        mParam3 = in.readString();
        mDescription = in.readString();
        mType = in.readInt();
        mCategory = in.readString();
        mTimeStamp = Objects.requireNonNull(in.readString());
        mColor = in.readInt();
        mIconUri = in.readString();
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getAppName() {
        return mAppName;
    }

    @NonNull
    public String getParam1() {
        return mParam1;
    }

    public String getParam2() {
        return mParam2;
    }

    public String getParam3() {
        return mParam3;
    }

    public String getDescription() {
        return mDescription;
    }

    @NonNull
    public Integer getType() {
        return mType;
    }

    @NonNull
    public String getTimeStamp() {
        return mTimeStamp;
    }

    public String getCategory() {
        return mCategory == null ? GlobalValues.INSTANCE.getCategory() : mCategory;
    }

    public void setCategory(@NonNull String mCategory) {
        this.mCategory = mCategory;
    }

    public void setId(@NonNull String mId) {
        this.mId = mId;
    }

    public void setAppName(@NonNull String mAppName) {
        this.mAppName = mAppName;
    }

    public void setParam1(@NonNull String mParam1) {
        this.mParam1 = mParam1;
    }

    public void setParam2(@NonNull String mParam2) {
        this.mParam2 = mParam2;
    }

    public void setParam3(@NonNull String mParam3) {
        this.mParam3 = mParam3;
    }

    public void setDescription(@NonNull String mDescription) {
        this.mDescription = mDescription;
    }

    public void setType(@NonNull Integer mType) {
        this.mType = mType;
    }

    public void setTimeStamp(@NonNull String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    @NonNull
    public Integer getColor() {
        return mColor;
    }

    public void setColor(@NonNull Integer mColor) {
        this.mColor = mColor;
    }

    public String getIconUri() {
        return mIconUri == null ? "" : mIconUri;
    }

    public void setIconUri(String mIconUri) {
        this.mIconUri = mIconUri;
    }

    public String getPackageName() {
        switch (mType) {
            case AnywhereType.Card.URL_SCHEME:
                return mParam2;
            case AnywhereType.Card.ACTIVITY:
            case AnywhereType.Card.QR_CODE:
                return mParam1;
            default:
                return "";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mAppName);
        dest.writeString(mParam1);
        dest.writeString(mParam2);
        dest.writeString(mParam3);
        dest.writeString(mDescription);
        dest.writeInt(mType);
        dest.writeString(mCategory);
        dest.writeString(mTimeStamp);
        dest.writeInt(mColor);
        dest.writeString(mIconUri);
    }

    public static final Parcelable.Creator<AnywhereEntity> CREATOR = new Parcelable.Creator<AnywhereEntity>() {

        @Override
        public AnywhereEntity createFromParcel(Parcel source) {
            return new AnywhereEntity(source);
        }

        @Override
        public AnywhereEntity[] newArray(int size) {
            return new AnywhereEntity[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "id=" + mId + ", " +
                "appName=" + mAppName + ", " +
                "param1=" + mParam1 + ", " +
                "param2=" + mParam2 + ", " +
                "param3=" + mParam3 + ", " +
                "desc=" + mDescription + ", " +
                "type=" + mType + ", " +
                "category=" + mCategory + ", " +
                "timeStamp=" + mTimeStamp + ", " +
                "color=" + mCategory;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof AnywhereEntity) {
            return mId.equals(((AnywhereEntity) obj).mId) &&
                    mAppName.equals(((AnywhereEntity) obj).mAppName) &&
                    mParam1.equals(((AnywhereEntity) obj).mParam1) &&
                    mParam2.equals(((AnywhereEntity) obj).mParam2) &&
                    mParam3.equals(((AnywhereEntity) obj).mParam3) &&
                    mDescription.equals(((AnywhereEntity) obj).mDescription) &&
                    mType.equals(((AnywhereEntity) obj).mType) &&
                    mCategory.equals(((AnywhereEntity) obj).mCategory) &&
                    mTimeStamp.equals(((AnywhereEntity) obj).mTimeStamp) &&
                    mColor.equals(((AnywhereEntity) obj).mColor);
        } else {
            return false;
        }
    }
}