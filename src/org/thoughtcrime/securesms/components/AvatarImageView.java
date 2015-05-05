package org.thoughtcrime.securesms.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.makeramen.RoundedDrawable;

import org.thoughtcrime.securesms.contacts.ContactPhotoFactory;
import org.thoughtcrime.securesms.recipients.Recipient;

public class AvatarImageView extends ImageView {

  public AvatarImageView(Context context) {
    super(context);
  }

  public AvatarImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setAvatar(Recipient recipient, boolean quickContactEnabled) {
    setAvatarImage(recipient);
    setAvatarClickHandler(recipient, quickContactEnabled);
  }

  private void setAvatarImage(Recipient recipient) {
    Bitmap contactPhoto = recipient.getContactPhoto();

    if ((contactPhoto != ContactPhotoFactory.getDefaultContactPhoto(getContext())) || (recipient.getName() == null)) {
      setImageDrawable(RoundedDrawable.fromBitmap(contactPhoto)
                                      .setScaleType(ScaleType.CENTER_CROP)
                                      .setOval(true));
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
