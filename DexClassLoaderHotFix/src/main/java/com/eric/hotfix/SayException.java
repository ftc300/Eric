package com.eric.hotfix;

/**
 * @author Danny 姜
 */
public class SayException implements ISay {
    @Override
    public String saySomething() {
        return "something wrong here!";
    }
}
