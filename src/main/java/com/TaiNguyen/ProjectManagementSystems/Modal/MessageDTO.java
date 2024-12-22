package com.TaiNguyen.ProjectManagementSystems.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class MessageDTO {
    private long id;
    private String content;
    private LocalDateTime createdAt;
    private Long chatId;
    private Long senderId;  // ID của người gửi
    private String senderFullname;
    private String senderEmail;
    private List<FileInfoDTO> senderFiles;
}
