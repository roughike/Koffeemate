package com.codemate.brewflop;

/**
 * Created by iiro on 6.10.2016.
 */
public interface SlackMessageCallback {
    void onMessagePostedToSlack();
    void onMessageError();
}