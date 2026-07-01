package com.clandro.api;

import com.clandro.shared.clandro.ClandroConstants;
import static com.clandro.shared.clandro.ClandroConstants.CLANDRO_API_PACKAGE_NAME;
import static com.clandro.shared.clandro.ClandroConstants.CLANDRO_PACKAGE_NAME;

public class ClandroAPIConstants {

    /**
     * Clandro:API Receiver name.
     */
    public static final String CLANDRO_API_RECEIVER_NAME = CLANDRO_API_PACKAGE_NAME + ".ClandroApiReceiver"; // Default to "com.clandro.api.ClandroApiReceiver"

    /** The Uri authority for Clandro:API app file shares */
    public static final String CLANDRO_API_FILE_SHARE_URI_AUTHORITY = CLANDRO_PACKAGE_NAME + ".sharedfiles"; // Default: "com.clandro.sharedfiles"

}
