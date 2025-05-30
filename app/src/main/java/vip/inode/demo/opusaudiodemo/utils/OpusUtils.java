package vip.inode.demo.opusaudiodemo.utils;

public class OpusUtils {
    private static OpusUtils opusUtils;

    static {
        System.loadLibrary("opusJni");
    }

    private OpusUtils() {
    }

    public static synchronized OpusUtils getInstance() {
        if (opusUtils == null) {
            opusUtils = new OpusUtils();
        }
        return opusUtils;
    }

    public native long createEncoder(int sampleRateInHz, int channelConfig, int complexity);
    public native long createDecoder(int sampleRateInHz, int channelConfig);
    public native int encode(long handle, short[] lin, int offset, byte[] encoded);
    public native int decode(long handle, byte[] encoded, short[] lin);
    public native void destroyEncoder(long handle);
    public native void destroyDecoder(long handle);
} 