package lavalink.server.player.services.sponsorblock;

import org.json.JSONArray;
import org.json.JSONObject;

public class Segment {
    private final String uuid;
    private final String category;
    private final String description;
    private final String actionType;
    private final String userId;
    private final long startTime;
    private final long endTime;

    private final int locked;
    private final int votes;

    public Segment(JSONObject json) {
        this.uuid = json.getString("UUID");
        this.category = json.getString("category");
        this.description = json.optString("description", null);
        this.actionType = json.optString("actionType", null);
        this.userId = json.optString("userID", null);

        JSONArray times = json.getJSONArray("segment");
        this.startTime = (long) (times.getFloat(0) * 1000);
        this.endTime = (long) (times.getFloat(1) * 1000);
        
        this.locked = json.optInt("locked", 0);
        this.votes = json.optInt("votes", 0);
    }

    public JSONObject encode() {
        String description = this.getDescription();
        String actionType = this.getActionType();
        String userId = this.getUserID();

        return new JSONObject()
        .put("uuid", this.uuid)
        .put("category", this.category)
        .put("description", description != null ? description : JSONObject.NULL)
        .put("actionType", actionType != null ? actionType : JSONObject.NULL)
        .put("userId", userId != null ? userId : JSONObject.NULL)
        .put("startTime", this.startTime)
        .put("endTime", this.endTime)
        .put("locked", this.locked)
        .put("votes", this.votes);
    }

    public String getUUID() {
        return this.uuid;
    }

    public String getCategory() {
        return this.category;
    }

    public String getDescription() {
        return this.description != null && !this.description.isBlank() ? this.description : null;
    }

    public String getActionType() {
        return this.actionType != null && !this.actionType.isBlank() ? this.actionType : null;
    }

    public String getUserID() {
        return this.userId != null && !this.userId.isBlank() ? this.userId : null;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public int getLocked() {
        return this.locked;
    }

    public int getVotes() {
        return this.votes;
    }
}
