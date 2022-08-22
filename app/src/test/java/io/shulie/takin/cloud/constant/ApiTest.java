package io.shulie.takin.cloud.constant;

import org.junit.jupiter.api.Test;

/**
 * api测试
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
class ApiTest {
    @Test
    void print() {
        System.out.println(Api.EMPTY_INSTANCE.getCommon().health());
        System.out.println(Api.EMPTY_INSTANCE.getCommon().version());
        System.out.println(Api.EMPTY_INSTANCE.getTicket().update());
        System.out.println(Api.EMPTY_INSTANCE.getTicket().generate());
        System.out.println(Api.EMPTY_INSTANCE.getWatchman().list());
        System.out.println(Api.EMPTY_INSTANCE.getWatchman().status());
        System.out.println(Api.EMPTY_INSTANCE.getWatchman().batchStatus());
        System.out.println(Api.EMPTY_INSTANCE.getWatchman().update());
        System.out.println(Api.EMPTY_INSTANCE.getWatchman().batchUpdate());
        System.out.println(Api.EMPTY_INSTANCE.getWatchman().resource());
        System.out.println(Api.EMPTY_INSTANCE.getWatchman().batchResource());

        System.out.println(Api.EMPTY_INSTANCE.getJob().getResource().lock());
        System.out.println(Api.EMPTY_INSTANCE.getJob().getResource().unlock());

        System.out.println(Api.EMPTY_INSTANCE.getJob().getFile().announce());
        System.out.println(Api.EMPTY_INSTANCE.getJob().getScript().announce());
        System.out.println(Api.EMPTY_INSTANCE.getJob().getCalibration().announce());

        System.out.println(Api.EMPTY_INSTANCE.getJob().getPressure().start());
        System.out.println(Api.EMPTY_INSTANCE.getJob().getPressure().stop());

        System.out.println(Api.EMPTY_INSTANCE.getJob().getExpand().getScript().build());
        System.out.println(Api.EMPTY_INSTANCE.getJob().getExpand().getPressure().getConfig());
        System.out.println(Api.EMPTY_INSTANCE.getJob().getExpand().getPressure().modifyConfig());
        System.out.println(Api.EMPTY_INSTANCE.getJob().getExpand().getResource().check());
        System.out.println(Api.EMPTY_INSTANCE.getJob().getExpand().getResource().exampleList());

        System.out.println(Api.EMPTY_INSTANCE.getNotify().getFile().failed());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getFile().updateProgress());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getFile().batchUpdateProgress());

        System.out.println(Api.EMPTY_INSTANCE.getNotify().getScript().verificationReport());

        System.out.println(Api.EMPTY_INSTANCE.getNotify().getCommand().ack());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getCommand().pop());

        System.out.println(Api.EMPTY_INSTANCE.getNotify().getWatchman().normal());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getWatchman().upload());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getWatchman().abnormal());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getWatchman().heartbeat());

        System.out.println(Api.EMPTY_INSTANCE.getNotify().getResource().getExample().stop());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getResource().getExample().start());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getResource().getExample().error());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getResource().getExample().heartbeat());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getResource().getExample().infoAndError());

        System.out.println(Api.EMPTY_INSTANCE.getNotify().getPressure().stop());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getPressure().start());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getPressure().getUsage().uploadFiel());

        System.out.println(Api.EMPTY_INSTANCE.getNotify().getPressure().getExample().stop());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getPressure().getExample().start());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getPressure().getExample().error());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getPressure().getExample().heartbeat());

        System.out.println(Api.EMPTY_INSTANCE.getNotify().getPressure().getMetrics().upload());
        System.out.println(Api.EMPTY_INSTANCE.getNotify().getPressure().getMetrics().oldUpload());

    }
}
