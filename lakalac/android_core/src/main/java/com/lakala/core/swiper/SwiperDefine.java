package com.lakala.core.swiper;

/**
 * Created by Vinchaos api on 14-1-4.
 */
public class SwiperDefine {
    public enum SwiperControllerState {
        /**
         * 空闲
         */
        STATE_IDLE(1),

        /**
         * 等设备就绪
         */
        STATE_WAITING_FOR_DEVICE(2),

        /**
         * 正在等待接收刷卡器数
         */
        STATE_RECORDING(3),

        /**
         * 正在解码
         */
        STATE_DECODING(4);

        private int state;

        private SwiperControllerState(int state) {
            this.state = state;
        }

        public int getSate() {
            return state;
        }
    }

    public enum SwiperControllerDecodeResult {
        /**
         * 刷卡失败
         */
        DECODE_SWIPE_FAIL(1),
        /**
         * CRC 校验错误
         */
        DECODE_CRC_ERROR(2),
        /**
         * 与刷卡器通信时发生错误
         */
        DECODE_COMM_ERROR(3),
        /**
         * 未知错误
         */
        DECODE_UNKNOWN_ERROR(4);

        @SuppressWarnings("unused")
        private int decode;

        private SwiperControllerDecodeResult(int decode) {
            this.decode = decode;
        }
    }

    public enum SwiperPortType {
        /**
         * 音频刷卡器
         */
        TYPE_AUDIO,
        /**
         * 无限支付猫
         */
        TYPE_WIFI,
        /**
         * 蓝牙刷卡器
         */
        TYPE_BLUETOOTH,
        /**
         * 拉风
         */
        TYPE_LKLMOBILE;
    }
}
