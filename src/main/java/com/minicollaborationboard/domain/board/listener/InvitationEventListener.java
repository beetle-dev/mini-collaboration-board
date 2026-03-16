package com.minicollaborationboard.domain.board.listener;

import com.minicollaborationboard.domain.board.event.InvitationEvent;
import com.minicollaborationboard.domain.board.service.BoardService;
import com.minicollaborationboard.common.service.EmailSender;
import com.minicollaborationboard.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvitationEventListener {

    @Value("${aws.ses.from}")
    private String from;

    @Value("${app.base-url}")
    private String appBaseUrl;

    private final BoardService boardService;
    private final EmailSender emailSender;

    private static final String EMAIL_SUBJECT = "Board 초대 메일";

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInvitationCreated(InvitationEvent invitationEvent) {

        Long boardId = invitationEvent.getBoardId();
        String inviteeEmail = invitationEvent.getInviteeEmail();
        String invitationUuid = invitationEvent.getInvitationUuid();

        String inviteLink = appBaseUrl + "/boards/invitation/" + invitationUuid + "/accept";

        try {

            String boardName = boardService.findById(boardId).orElseThrow(() ->
                    new ResourceNotFoundException("보드를 찾을 수 없습니다.")).getName();

            String htmlBody = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "  <body style=\"margin:0; padding:0; background-color:#f4f6f8; font-family: Arial, Helvetica, sans-serif;\">\n" +
                    "    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#f4f6f8; padding:40px 0;\">\n" +
                    "      <tr>\n" +
                    "        <td align=\"center\">\n" +
                    "          <!-- Container -->\n" +
                    "          <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#ffffff; border-radius:8px; overflow:hidden;\">\n" +
                    "            \n" +
                    "            <!-- Header -->\n" +
                    "            <tr>\n" +
                    "              <td style=\"padding:24px 32px; background-color:#1f2937;\">\n" +
                    "                <h1 style=\"margin:0; color:#ffffff; font-size:20px; font-weight:bold;\">\n" +
                    "                  Board Invitation\n" +
                    "                </h1>\n" +
                    "              </td>\n" +
                    "            </tr>\n" +
                    "\n" +
                    "            <!-- Body -->\n" +
                    "            <tr>\n" +
                    "              <td style=\"padding:32px; color:#111827;\">\n" +
                    "                <p style=\"margin:0 0 16px 0; font-size:15px; line-height:1.6;\">\n" +
                    "                  안녕하세요,\n" +
                    "                </p>\n" +
                    "\n" +
                    "                <p style=\"margin:0 0 16px 0; font-size:15px; line-height:1.6;\">" +
                    "                  <strong> " + boardName + "</strong> 보드에 초대되었습니다.\n" +
                    "                </p>\n" +
                    "\n" +
                    "                <p style=\"margin:0 0 24px 0; font-size:15px; line-height:1.6;\">\n" +
                    "                  아래 버튼을 클릭하여 보드에 참여하세요.\n" +
                    "                </p>\n" +
                    "\n" +
                    "                <!-- Button -->\n" +
                    "                <table cellpadding=\"0\" cellspacing=\"0\" style=\"margin:24px 0;\">\n" +
                    "                  <tr>\n" +
                    "                    <td align=\"center\">\n" +
                    "                      <a href=\"{{inviteLink}}\"\n" +
                    "                         style=\"\n" +
                    "                           display:inline-block;\n" +
                    "                           padding:12px 24px;\n" +
                    "                           background-color:#2563eb;\n" +
                    "                           color:#ffffff;\n" +
                    "                           text-decoration:none;\n" +
                    "                           font-size:14px;\n" +
                    "                           font-weight:bold;\n" +
                    "                           border-radius:6px;\n" +
                    "                         \">\n" +
                    "                        보드 초대 수락하기\n" +
                    "                      </a>\n" +
                    "                    </td>\n" +
                    "                  </tr>\n" +
                    "                </table>\n" +
                    "\n" +
                    "                <p style=\"margin:24px 0 0 0; font-size:13px; color:#6b7280; line-height:1.5;\">\n" +
                    "                  버튼이 동작하지 않으면 아래 링크를 복사하여 브라우저에 붙여넣으세요:\n" +
                    "                </p>\n" +
                    "\n" +
                    "                <p style=\"margin:8px 0 0 0; font-size:13px; word-break:break-all;\">\n" +
                    "                  <a href=\"{{inviteLink}}\" style=\"color:#2563eb; text-decoration:none;\">\n" +
                    "                    {{inviteLink}}\n" +
                    "                  </a>\n" +
                    "                </p>\n" +
                    "              </td>\n" +
                    "            </tr>\n" +
                    "\n" +
                    "            <!-- Footer -->\n" +
                    "            <tr>\n" +
                    "              <td style=\"padding:20px 32px; background-color:#f9fafb; font-size:12px; color:#6b7280;\">\n" +
                    "                이 초대는 발신 일시부터 3일까지 유효합니다.<br/>\n" +
                    "                본 메일은 발신 전용입니다.\n" +
                    "              </td>\n" +
                    "            </tr>\n" +
                    "\n" +
                    "          </table>\n" +
                    "          <!-- /Container -->\n" +
                    "        </td>\n" +
                    "      </tr>\n" +
                    "    </table>\n" +
                    "  </body>\n" +
                    "</html>\n";

            htmlBody = htmlBody.replace("{{inviteLink}}", inviteLink);

            emailSender.sendHtmlMessage(from, inviteeEmail, EMAIL_SUBJECT, htmlBody);
        } catch (Exception e) {

            log.error("초대 이메일 발송 실패: boardId = {}, email = {}", boardId, inviteeEmail);
            log.error(e.getMessage());
        }

    }
}
