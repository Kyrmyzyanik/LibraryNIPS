package com.example.nips.requests;

import java.net.URI;

/**
 * Interface to standardize implementations
 */
public interface ResponseHandlerInterface {

    /**
     * Returns data whether request completed successfully
     *
     * @param response HttpResponse object with data
     * @throws java.io.IOException if retrieving data from response fails
     */
  //  void sendResponseMessage(HttpResponse response) throws IOException;

    /**
     * Notifies callback, that request started execution
     */
    void sendStartMessage();

    /**
     * Notifies callback, that request was completed and is being removed from thread pool
     */
    void sendFinishMessage();

    /**
     * Notifies callback, that request (mainly uploading) has progressed
     *
     * @param bytesWritten number of written bytes
     * @param bytesTotal   number of total bytes to be written
     */
    void sendProgressMessage(long bytesWritten, long bytesTotal);

    /**
     * Notifies callback, that request was cancelled
     */
    void sendCancelMessage();


    /**
     * Returns whether the handler is asynchronous or synchronous
     *
     * @return boolean if the ResponseHandler is running in synchronous mode
     */
    boolean getUseSynchronousMode();

    /**
     * Can set, whether the handler should be asynchronous or synchronous
     *
     * @param useSynchronousMode whether data should be handled on background Thread on UI Thread
     */
    void setUseSynchronousMode(boolean useSynchronousMode);

    /**
     * Returns whether the handler should be executed on the pool's thread
     * or the UI thread
     *
     * @return boolean if the ResponseHandler should run on pool's thread
     */
    boolean getUsePoolThread();

    /**
     * Sets whether the handler should be executed on the pool's thread or the
     * UI thread
     *
     * @param usePoolThread if the ResponseHandler should run on pool's thread
     */
    void setUsePoolThread(boolean usePoolThread);

    /**
     * This method is called once by the system when the response is about to be
     * processed by the system. The library makes sure that a single response
     * is pre-processed only once.
     * <p>&nbsp;</p>
     * Please note: pre-processing does NOT run on the main thread, and thus
     * any UI activities that you must perform should be properly dispatched to
     * the app's UI thread.
     *
     * @param instance An instance of this response object
     */
    void onPreProcessResponse(ResponseHandlerInterface instance);

    /**
     * This method is called once by the system when the request has been fully
     * sent, handled and finished. The library makes sure that a single response
     * is post-processed only once.
     * <p>&nbsp;</p>
     * Please note: post-processing does NOT run on the main thread, and thus
     * any UI activities that you must perform should be properly dispatched to
     * the app's UI thread.
     *
     * @param instance An instance of this response object
     */
    void onPostProcessResponse(ResponseHandlerInterface instance);

    /**
     * Will retrieve TAG Object if it's not already freed from memory
     *
     * @return Object TAG or null if it's been garbage collected
     */
    Object getTag();

    /**
     * Will set TAG to ResponseHandlerInterface implementation, which can be then obtained
     * in implemented methods, such as onSuccess, onFailure, ...
     *
     * @param TAG Object to be set as TAG, will be placed in WeakReference
     */
    void setTag(Object TAG);
}