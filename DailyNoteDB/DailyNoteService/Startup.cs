using Microsoft.Owin;
using Owin;

[assembly: OwinStartup(typeof(DailyNoteService.Startup))]

namespace DailyNoteService
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureMobileApp(app);
        }
    }
}