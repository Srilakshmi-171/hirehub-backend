
package com.hirehub.util;

public class EmailTemplateUtil {

    private EmailTemplateUtil() {
    }

    public static String applicationConfirmation(
            String candidateName,
            String jobTitle,
            String companyName) {

        return """
                <!DOCTYPE html>
                <html>
                <body style="font-family: Arial, sans-serif;
                             background-color: #f4f4f4;
                             padding: 20px;">

                    <div style="max-width: 600px;
                                margin: auto;
                                background: white;
                                padding: 25px;
                                border-radius: 8px;">

                        <h2 style="color: #2563eb;">
                            HireHub
                        </h2>

                        <h3>Application Submitted Successfully!</h3>

                        <p>Hello %s,</p>

                        <p>
                            Your application has been successfully
                            submitted.
                        </p>

                        <p><b>Job:</b> %s</p>
                        <p><b>Company:</b> %s</p>

                        <p>
                            You can track your application status
                            through your HireHub account.
                        </p>

                        <p>Best regards,<br>HireHub Team</p>

                    </div>
                </body>
                </html>
                """.formatted(
                escapeHtml(candidateName),
                escapeHtml(jobTitle),
                escapeHtml(companyName)
        );
    }
    

public static String applicationStatusUpdate(
        String candidateName,
        String jobTitle,
        String status) {

    return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif;
                         padding: 20px;">

                <div style="max-width: 600px;
                            margin: auto;
                            padding: 25px;
                            border: 1px solid #ddd;
                            border-radius: 8px;">

                    <h2 style="color: #2563eb;">HireHub</h2>

                    <h3>Application Status Update</h3>

                    <p>Hello %s,</p>

                    <p>Your application status has been updated.</p>

                    <p><b>Job:</b> %s</p>
                    <p><b>New Status:</b> %s</p>

                    <p>
                        Please log in to HireHub to view your
                        application details.
                    </p>

                    <p>Best regards,<br>HireHub Team</p>

                </div>
            </body>
            </html>
            """.formatted(
            escapeHtml(candidateName),
            escapeHtml(jobTitle),
            escapeHtml(status)
    );
}


public static String interviewScheduled(
        String candidateName,
        String jobTitle,
        String interviewTitle,
        String scheduledAt,
        String mode,
        String meetingLink) {

    return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif;
                         padding: 20px;">

                <div style="max-width: 600px;
                            margin: auto;
                            padding: 25px;
                            border: 1px solid #ddd;
                            border-radius: 8px;">

                    <h2 style="color: #2563eb;">HireHub</h2>

                    <h3>Interview Scheduled!</h3>

                    <p>Hello %s,</p>

                    <p>Your interview has been scheduled.</p>

                    <p><b>Job:</b> %s</p>
                    <p><b>Interview:</b> %s</p>
                    <p><b>Date and Time:</b> %s</p>
                    <p><b>Mode:</b> %s</p>
                    <p><b>Meeting Link:</b> %s</p>

                    <p>Please be available on time.</p>

                    <p>Best regards,<br>HireHub Team</p>

                </div>
            </body>
            </html>
            """.formatted(
            escapeHtml(candidateName),
            escapeHtml(jobTitle),
            escapeHtml(interviewTitle),
            escapeHtml(scheduledAt),
            escapeHtml(mode),
            escapeHtml(meetingLink)
    );
}


public static String interviewRescheduled(
        String candidateName,
        String interviewTitle,
        String newScheduledAt,
        String reason) {

    return """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #2563eb;">HireHub</h2>
                <h3>Interview Rescheduled</h3>

                <p>Hello %s,</p>
                <p>Your interview has been rescheduled.</p>

                <p><b>Interview:</b> %s</p>
                <p><b>New Date and Time:</b> %s</p>
                <p><b>Reason:</b> %s</p>

                <p>Best regards,<br>HireHub Team</p>
            </body>
            </html>
            """.formatted(
            escapeHtml(candidateName),
            escapeHtml(interviewTitle),
            escapeHtml(newScheduledAt),
            escapeHtml(reason)
    );
}


public static String interviewCancelled(
        String candidateName,
        String interviewTitle,
        String jobTitle) {

    return """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #2563eb;">HireHub</h2>
                <h3>Interview Cancelled</h3>

                <p>Hello %s,</p>
                <p>Your interview has been cancelled.</p>

                <p><b>Job:</b> %s</p>
                <p><b>Interview:</b> %s</p>

                <p>Please check your HireHub account for updates.</p>

                <p>Best regards,<br>HireHub Team</p>
            </body>
            </html>
            """.formatted(
            escapeHtml(candidateName),
            escapeHtml(jobTitle),
            escapeHtml(interviewTitle)
    );
}


public static String jobAlert(
        String candidateName,
        String jobTitle,
        String companyName,
        String location) {

    return """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <div style="max-width: 600px;
                            margin: auto;
                            padding: 25px;
                            border: 1px solid #ddd;
                            border-radius: 8px;">

                    <h2 style="color: #2563eb;">HireHub</h2>
                    <h3>New Job Matching Your Preferences!</h3>

                    <p>Hello %s,</p>

                    <p>A new job matching your preferences is available.</p>

                    <p><b>Job:</b> %s</p>
                    <p><b>Company:</b> %s</p>
                    <p><b>Location:</b> %s</p>

                    <p>Log in to HireHub to explore this opportunity.</p>

                    <p>Best regards,<br>HireHub Team</p>
                </div>
            </body>
            </html>
            """.formatted(
            escapeHtml(candidateName),
            escapeHtml(jobTitle),
            escapeHtml(companyName),
            escapeHtml(location)
    );
}


    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
