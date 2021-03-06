/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.util;

import java.io.Serializable;

/**
 * A currency corresponding to an <a href="http://en.wikipedia.org/wiki/ISO_4217">ISO 4217</a>
 * currency code such as "EUR" or "USD".
 */
public final class Currency implements Serializable {
    private static final long serialVersionUID = -158308464356906721L;

    private static final HashMap<String, Currency> codesToCurrencies = new HashMap<String, Currency>();
    private static final HashMap<Locale, Currency> localesToCurrencies = new HashMap<Locale, Currency>();

    private final String currencyCode;

    private Currency(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * Returns the {@code Currency} instance for the given ISO 4217 currency code.
     * @throws IllegalArgumentException
     *             if the currency code is not a supported ISO 4217 currency code.
     */
    public static Currency getInstance(String currencyCode) {
        synchronized (codesToCurrencies) {
            Currency currency = codesToCurrencies.get(currencyCode);
            if (currency == null) {
                currency = new Currency(currencyCode);
                codesToCurrencies.put(currencyCode, currency);
            }
            return currency;
        }
    }

    /**
     * Returns the {@code Currency} instance for this {@code Locale}'s country.
     * @throws IllegalArgumentException
     *             if the locale's country is not a supported ISO 3166 country.
     */
    public static Currency getInstance(Locale locale) {
        synchronized (localesToCurrencies) {
            Currency currency = localesToCurrencies.get(locale);
            if (currency != null) {
                return currency;
            }
            String currencyCode = getCurrencyCodeForLocale(locale);
            if (currencyCode == null) {
                throw new IllegalArgumentException("Unsupported ISO 3166 country: " + locale);
            } else if (currencyCode.equals("XXX")) {
                return null;
            }
            Currency result = getInstance(currencyCode);
            localesToCurrencies.put(locale, result);
            return result;
        }
    }

    private static native String getCurrencyCodeForLocale(Locale locale) /*-[
      NSLocale *nativeLocale =
          AUTORELEASE([[NSLocale alloc] initWithLocaleIdentifier:[locale description]]);
      NSNumberFormatter *formatter = AUTORELEASE([[NSNumberFormatter alloc] init]);
      [formatter setNumberStyle:NSNumberFormatterCurrencyStyle];
      [formatter setLocale:nativeLocale];
      return [formatter currencyCode];
    ]-*/;

    private static native String getCurrencySymbolForLocale(Locale locale) /*-[
      NSLocale *nativeLocale =
          AUTORELEASE([[NSLocale alloc] initWithLocaleIdentifier:[locale description]]);
      NSNumberFormatter *formatter = AUTORELEASE([[NSNumberFormatter alloc] init]);
      [formatter setNumberStyle:NSNumberFormatterCurrencyStyle];
      [formatter setLocale:nativeLocale];
      return [formatter currencySymbol];
    ]-*/;

    /**
     * Returns this currency's ISO 4217 currency code.
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Equivalent to {@code getSymbol(Locale.getDefault())}.
     * See "<a href="../util/Locale.html#default_locale">Be wary of the default locale</a>".
     */
    public String getSymbol() {
        return getSymbol(Locale.getDefault());
    }

    /**
     * Returns the localized currency symbol for this currency in {@code locale}.
     * That is, given "USD" and Locale.US, you'd get "$", but given "USD" and a non-US locale,
     * you'd get "US$".
     *
     * <p>If the locale only specifies a language rather than a language and a country (such as
     * {@code Locale.JAPANESE} or {new Locale("en", "")} rather than {@code Locale.JAPAN} or
     * {new Locale("en", "US")}), the ISO 4217 currency code is returned.
     *
     * <p>If there is no locale-specific currency symbol, the ISO 4217 currency code is returned.
     */
    public String getSymbol(Locale locale) {
        if (locale.getCountry().length() == 0) {
            return currencyCode;
        }
        String symbol = getCurrencySymbolForLocale(locale);
        return symbol != null ? symbol : currencyCode;
    }

    /**
     * Returns the default number of fraction digits for this currency.
     * For instance, the default number of fraction digits for the US dollar is 2 because there are
     * 100 US cents in a US dollar. For the Japanese Yen, the number is 0 because coins smaller
     * than 1 Yen became invalid in 1953. In the case of pseudo-currencies, such as
     * IMF Special Drawing Rights, -1 is returned.
     *
    TODO(user): enable when ICU currency tables are available.
    public int getDefaultFractionDigits() {
        return com.ibm.icu.util.Currency.getInstance(currencyCode).getDefaultFractionDigits();
    }
    */

    /**
     * Returns this currency's ISO 4217 currency code.
     */
    @Override
    public String toString() {
        return currencyCode;
    }
}
