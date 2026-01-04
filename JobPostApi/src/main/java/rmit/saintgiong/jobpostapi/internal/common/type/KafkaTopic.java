package rmit.saintgiong.jobpostapi.internal.common.type;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class KafkaTopic {
    public static final String JOB_POST_UPDATED_TOPIC = "JM_POST_UPDATED";
    public static final String JOB_POST_UPDATED_REPLY_TOPIC = "JM_POST_UPDATED_REPLIED";
    public static final String JOB_POST_ADDED_TOPIC = "JM_POST_ADDED";
    public static final String JOB_POST_ADDED_REPLY_TOPIC = "JM_POST_ADDED_REPLIED";
}
