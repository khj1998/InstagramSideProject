package CloneProject.InstagramClone.websocket.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String senderName;
    private String receiverName;
    private String message;
    private String data;
    private Status status;
}
