/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.zoo.swan.core.logo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zoo.swan.common.constant.CommonConstant;
import org.zoo.swan.common.utils.VersionUtils;

/**
 * The cat logo.
 *
 * @author dzc
 */
public class SwanLogo {

    private static final String CAT_LOGO = "\n" +
            "\n" + 
            "          _____                   _______                  _______                   _____                    _____                    _____                _____          \n" + 
            "         /\\    \\                 /::\\    \\                /::\\    \\                 /\\    \\                  /\\    \\                  /\\    \\              /\\    \\         \n" + 
            "        /::\\    \\               /::::\\    \\              /::::\\    \\               /::\\____\\                /::\\    \\                /::\\    \\            /::\\    \\        \n" + 
            "        \\:::\\    \\             /::::::\\    \\            /::::::\\    \\             /:::/    /               /::::\\    \\              /::::\\    \\           \\:::\\    \\       \n" + 
            "         \\:::\\    \\           /::::::::\\    \\          /::::::::\\    \\           /:::/    /               /::::::\\    \\            /::::::\\    \\           \\:::\\    \\      \n" + 
            "          \\:::\\    \\         /:::/~~\\:::\\    \\        /:::/~~\\:::\\    \\         /:::/    /               /:::/\\:::\\    \\          /:::/\\:::\\    \\           \\:::\\    \\     \n" + 
            "           \\:::\\    \\       /:::/    \\:::\\    \\      /:::/    \\:::\\    \\       /:::/    /               /:::/  \\:::\\    \\        /:::/__\\:::\\    \\           \\:::\\    \\    \n" + 
            "            \\:::\\    \\     /:::/    / \\:::\\    \\    /:::/    / \\:::\\    \\     /:::/    /               /:::/    \\:::\\    \\      /::::\\   \\:::\\    \\          /::::\\    \\   \n" + 
            "             \\:::\\    \\   /:::/____/   \\:::\\____\\  /:::/____/   \\:::\\____\\   /:::/    /               /:::/    / \\:::\\    \\    /::::::\\   \\:::\\    \\        /::::::\\    \\  \n" + 
            "              \\:::\\    \\ |:::|    |     |:::|    ||:::|    |     |:::|    | /:::/    /               /:::/    /   \\:::\\    \\  /:::/\\:::\\   \\:::\\    \\      /:::/\\:::\\    \\ \n" + 
            "_______________\\:::\\____\\|:::|____|     |:::|    ||:::|____|     |:::|    |/:::/____/               /:::/____/     \\:::\\____\\/:::/  \\:::\\   \\:::\\____\\    /:::/  \\:::\\____\\\n" + 
            "\\::::::::::::::::::/    / \\:::\\    \\   /:::/    /  \\:::\\    \\   /:::/    / \\:::\\    \\               \\:::\\    \\      \\::/    /\\::/    \\:::\\  /:::/    /   /:::/    \\::/    /\n" + 
            " \\::::::::::::::::/____/   \\:::\\    \\ /:::/    /    \\:::\\    \\ /:::/    /   \\:::\\    \\               \\:::\\    \\      \\/____/  \\/____/ \\:::\\/:::/    /   /:::/    / \\/____/ \n" + 
            "  \\:::\\~~~~\\~~~~~~          \\:::\\    /:::/    /      \\:::\\    /:::/    /     \\:::\\    \\               \\:::\\    \\                       \\::::::/    /   /:::/    /          \n" + 
            "   \\:::\\    \\                \\:::\\__/:::/    /        \\:::\\__/:::/    /       \\:::\\    \\               \\:::\\    \\                       \\::::/    /   /:::/    /           \n" + 
            "    \\:::\\    \\                \\::::::::/    /          \\::::::::/    /         \\:::\\    \\               \\:::\\    \\                      /:::/    /    \\::/    /            \n" + 
            "     \\:::\\    \\                \\::::::/    /            \\::::::/    /           \\:::\\    \\               \\:::\\    \\                    /:::/    /      \\/____/             \n" + 
            "      \\:::\\    \\                \\::::/    /              \\::::/    /             \\:::\\    \\               \\:::\\    \\                  /:::/    /                           \n" + 
            "       \\:::\\____\\                \\::/____/                \\::/____/               \\:::\\____\\               \\:::\\____\\                /:::/    /                            \n" + 
            "        \\::/    /                 ~~                       ~~                      \\::/    /                \\::/    /                \\::/    /                             \n" + 
            "         \\/____/                                                                    \\/____/                  \\/____/                  \\/____/                              \n" + 
            "                                                                                                                                                                           \n" + 
            "" ;

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwanLogo.class);

    public void logo() {
        String bannerText = buildBannerText();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(bannerText);
        } else {
            System.out.print(bannerText);
        }
    }

    private String buildBannerText() {
        return CommonConstant.LINE_SEPARATOR
                + CommonConstant.LINE_SEPARATOR
                + CAT_LOGO
                + CommonConstant.LINE_SEPARATOR
                + " :: Cat :: (v" + VersionUtils.getVersion(getClass(), "1.0.1") + ")"
                + CommonConstant.LINE_SEPARATOR;
    }

}
