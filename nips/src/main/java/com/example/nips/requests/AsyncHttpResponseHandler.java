package com.example.nips.requests;

/**
 * Created by kyrmyzy on 21/10/2016.
 */


@SuppressWarnings("DesignForExtension")
public abstract class AsyncHttpResponseHandler  implements  ResponseHandlerInterface {

    /**
     * Fired when a request returns successfully, override to handle in your own code
     *
     * @param statusCode   the status code of the response
     * @param responseBody the body of the HTTP response from the server
     */
    public abstract void onSuccess(int statusCode, byte[] responseBody);



}

