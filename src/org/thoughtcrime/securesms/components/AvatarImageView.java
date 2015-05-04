package org.thoughtcrime.securesms.components;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.makeramen.RoundedDrawable;

import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.contacts.ContactPhotoFactory;
import org.thoughtcrime.securesms.recipients.Recipient;

public class AvatarImageView extends ImageView {

  private static final ScaleType[] SCALE_TYPES = {
      ScaleType.MATRIX,
      ScaleType.FIT_XY,
      ScaleType.FIT_START,
      ScaleType.FIT_CENTER,
      ScaleType.FIT_END,
      ScaleType.CENTER,
      ScaleType.CENTER_CROP,
      ScaleType.CENTER_INSIDE
  };

  private boolean   isOval    = false;
  private ScaleType scaleType = ScaleType.FIT_CENTER;

  public AvatarImageView(Context context) {
    super(context);
  }

  public AvatarImageView(Context context, AttributeSet attrs) {
    super(context, attrs);

    TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView, 0, 0);
    int        scaleTypeIndex   = styledAttributes.getInt(R.styleable.AvatarImageView_android_scaleType, -1);

    if (scaleTypeIndex >= 0) {
      this.scaleType = SCALE_TYPES[scaleTypeIndex];
    }

    this.isOval = styledAttributes.getBoolean(R.styleable.AvatarImageView_circle, false);

    styledAttributes.recycle();
  }

  public void setAvatar(Recipient recipient, boolean quickContactEnabled) {
    setAvatarImage(recipient);
    setAvatarClickHandler(recipient, quickContactEnabled);
  }

  private void setAvatarImage(Recipient recipient) {
    Bitmap contactPhoto = recipient.getContactPhoto();

    if ((contactPhoto != ContactPhotoFactory.getDefaultContactPhoto(getContext())) || (recipient.getName() == null)) {
      setImageDrawable(RoundedDrawable.fromBitmap(contactPhoto).setScaleType(scaleType).setOval(isOval));
    } else {
      setImageDrawable(TextDrawable.builder()
                                   .beginConfig()
                                   .toUpperCase()
                                   .endConfig()
                                   .buildRound(String.valueOf(recipient.getName().charAt(0)),
                                               ColorGenerator.MATERIAL.getColor(recipient.getName())));
    }
  }

  private void setAvatarClickHandler(final Recipient recipient, boolean quickContactEnabled) {
    if (!recipient.isGroupRecipient() && quickContactEnabled) {
      setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (recipient.getContactUri() != null) {
            ContactsContract.QuickContact.showQuickContact(getContext(), AvatarImageView.this, recipient.getContactUri(), ContactsContract.QuickContact.MODE_LARGE, null);
          } else {
            final Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, recipient.getNumber());
            intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
            getContext().startActivity(intent);
          }
        }
      });
    } else {
      setOnClickListener(null);
    }
  }

}
