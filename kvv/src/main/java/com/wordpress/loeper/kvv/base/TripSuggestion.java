/*
 * This file is part of the unofficial Java KVV API.
 * 
 * Copyright (c) 2014, Martin L�per
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.wordpress.loeper.kvv.base;

public class TripSuggestion {
	private String mValue;
	private String mHtmlValue;
	private String mPlainValue;
	
	public static enum Type {ORIGIN, DESTINATION};
	
	public TripSuggestion(String pValue, String pHtmlValue, String pPlainValue) {
		this.mValue = pValue;
		this.mHtmlValue = pHtmlValue;
		this.mPlainValue = pPlainValue;
	}
	
	public String getValue() {
		return this.mValue;
	}
	
	public String getHtmlValue() {
		return this.mHtmlValue;
	}
	
	/**
	 * Returns the plain value which has the additional information whether the location is a Stop or Address etc.
	 * This is really bad design, but there is currently no way to fix this, because the names differ for each language.
	 * @return the full station name
	 */
	public String getPlainValue() {
		return this.mPlainValue;
	}
	
	@Override
	public String toString() {
		return this.getPlainValue();
	}
}
