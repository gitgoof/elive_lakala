/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lakala.core.scanner.zxing.result;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.lakala.core.scanner.zxing.TwoDimenHelper;


/**
 * Manufactures Android-specific handlers based on the barcode content's type.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ResultHandlerFactory {
  private ResultHandlerFactory() {
  }

  public static ResultHandler makeResultHandler(TwoDimenHelper activity, Result rawResult) {
    ParsedResult result = parseResult(rawResult);
    switch (result.getType()) {
      case ADDRESSBOOK:
        return new AddressBookResultHandler(activity.getActivity(), result);
      case EMAIL_ADDRESS:
        return new EmailAddressResultHandler(activity.getActivity(),result);
      case PRODUCT:
        return new ProductResultHandler(activity.getActivity(), result, rawResult);
      case URI:
        return new URIResultHandler(activity.getActivity(), result);
      case WIFI:
        return new WifiResultHandler(activity, result);
      case GEO:
        return new GeoResultHandler(activity.getActivity(), result);
      case TEL:
        return new TelResultHandler(activity.getActivity(), result);
      case SMS:
        return new SMSResultHandler(activity.getActivity(), result);
      case CALENDAR:
        return new CalendarResultHandler(activity.getActivity(), result);
      case ISBN:
        return new ISBNResultHandler(activity.getActivity(), result, rawResult);
      default:
        return new TextResultHandler(activity.getActivity(), result, rawResult);
    }
  }

  private static ParsedResult parseResult(Result rawResult) {
    return ResultParser.parseResult(rawResult);
  }
}
