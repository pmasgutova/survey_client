package com.lockscreensecurity;

import java.io.Serializable;

public class AnswerValue implements Serializable{
	private static final long serialVersionUID = -4304473319455825507L;

	public int questionId;

	public String answer;

	public AnswerValue(int qID, String aS) {
		questionId = qID;
		answer = aS;
	}
}
