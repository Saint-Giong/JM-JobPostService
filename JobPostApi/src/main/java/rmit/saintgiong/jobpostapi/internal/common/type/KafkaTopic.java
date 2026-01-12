package rmit.saintgiong.jobpostapi.internal.common.type;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class KafkaTopic {
    public static final String JOB_POST_UPDATED_TOPIC = "JM_POST_UPDATED";
    public static final String JOB_POST_UPDATED_REPLY_TOPIC = "JM_POST_UPDATED_REPLIED";
    public static final String JOB_POST_ADDED_TOPIC = "JM_POST_ADDED";
    public static final String JOB_POST_ADDED_REPLY_TOPIC = "JM_POST_ADDED_REPLIED";

    public static final String GET_PROFILE_REQUEST = "JM_GET_PROFILE_REQUEST";
    public static final String GET_PROFILE_RESPONSE = "JM_GET_PROFILE_RESPONSE";

    public static final String GET_ALL_PROFILE_REQUEST = "JM_GET_ALL_PROFILE_REQUEST";
    public static final String GET_ALL_PROFILE_RESPONSE = "JM_GET_ALL_PROFILE_RESPONSE";


}
