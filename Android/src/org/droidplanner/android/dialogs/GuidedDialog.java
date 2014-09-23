package org.droidplanner.android.dialogs;

import org.droidplanner.R;
import org.droidplanner.android.activities.helpers.SuperUI;
import org.droidplanner.android.communication.connection.SocketIOConnection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class GuidedDialog extends DialogFragment {

	public interface GuidedDialogListener {
		public void onForcedGuidedPoint(LatLng coord);
	}

	private GuidedDialogListener listener;
	private LatLng coord;

	public void setCoord(LatLng coord) {
		this.coord = coord;
	}

	public void setListener(GuidedDialogListener mListener) {
		this.listener = mListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setMessage(R.string.guided_mode_warning)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						if (coord != null) {
							
							// TODO
							// The easiest way is make listener public and accessible from class "SuperUI.CallbackImpl"
							// Once it's accessible, we can send our own coord to drone using the command:
							// "listener.onForcedGuidedPoint(our_own_coord_from_PC);"
							
							String debug_msg = String.format("Longitude: %12.7f, Latitude: %12.7f", coord.longitude, coord.latitude);
							SocketIOConnection.getInstance().send_msg("hi, debug message", debug_msg);
							
							listener.onForcedGuidedPoint(coord);
						}
					}
				}).setNegativeButton(android.R.string.cancel, null);

		return builder.create();
	}
}