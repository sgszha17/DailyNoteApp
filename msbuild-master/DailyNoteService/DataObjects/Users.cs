using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Microsoft.Azure.Mobile.Server;

namespace DailyNoteService.DataObjects
{
    public class Users : EntityData
    {
        public String Email { get; set; }

        public String Username { get; set; }

        public String UserId { get; set; }

        public int RegisterType { get; set; }

        public String UseGoogle { get; set; }

        public String UseFB { get; set; }

        public String UseWechat { get; set; }

        public String AdditionalData { get; set; }
    }
}