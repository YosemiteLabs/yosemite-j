/*
 * Copyright (c) 2017-2018 PLACTAL.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.yosemite.util;


public final class Consts {

    public static final String YOSEMITE_SYSTEM_CONTRACT = "yosemite";
    public static final String YOSEMITE_NATIVE_TOKEN_CONTRACT = "yx.ntoken";
    public static final String YOSEMITE_TOKEN_CONTRACT = "yx.token";
    public static final String YOSEMITE_DIGITAL_CONTRACT_CONTRACT = "yx.dcontract";
    public static final String YOSEMITE_TOKEN_ESCROW_CONTRACT = "yx.escrow";
    public static final String YOSEMITE_NFT_CONTRACT = "yx.nft";

    public static final String DEFAULT_KEYOS_HTTP_URL = "http://127.0.0.1:8900";
    public static final String DEFAULT_WALLET_NAME = "default";
    public static final boolean DEFAULT_SAVE_PASSWORD = true;

    public static final int TX_EXPIRATION_IN_MILLIS = 1000 * 60 * 2; // 2 minutes (from cleos)

    public static final String DEFAULT_SYMBOL_STRING = "DKRW";
    public static final int DEFAULT_SYMBOL_PRECISION = 2;
}
