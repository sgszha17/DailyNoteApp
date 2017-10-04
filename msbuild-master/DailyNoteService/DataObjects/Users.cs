using Microsoft.Azure.Mobile.Server;

namespace DailyNoteService.DataObjects
{
    public class Users : EntityData
    {
        public string Email { get; set; }

        public string Username { get; set; }

        public string Password { get; set; }

        public int RegisterType { get; set; }

        public bool UseGoogle { get; set; }

        public bool UseFB { get; set; }

        public bool UseWechat { get; set; }

        public string AdditionalData { get; set; }
    }
}