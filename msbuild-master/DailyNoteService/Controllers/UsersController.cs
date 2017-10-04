using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Controllers;
using System.Web.Http.OData;
using Microsoft.Azure.Mobile.Server;
using DailyNoteService.DataObjects;
using DailyNoteService.Models;

namespace DailyNoteService.Controllers
{
    public class UsersController : TableController<Users>
    {
        protected override void Initialize(HttpControllerContext controllerContext)
        {
            base.Initialize(controllerContext);
            DailyNoteContext context = new DailyNoteContext();
            DomainManager = new EntityDomainManager<Users>(context, Request);
        }

        // GET tables
        public IQueryable<Users> GetAllUsers()
        {
            return Query();
        }

   
        public SingleResult<Users> GetUsers(string id)
        {
            return Lookup(id);
        }

      
        public Task<Users> PatchUsers(string id, Delta<Users> patch)
        {
            return UpdateAsync(id, patch);
        }

        // POST tables/TodoItem
        public async Task<IHttpActionResult> PostUsers(Users item)
        {
            Users current = await InsertAsync(item);
            return CreatedAtRoute("Tables", new { id = current.Id }, current);
        }

        // DELETE tables/TodoItem/48D68C86-6EA6-4C25-AA33-223FC9A27959
        public Task DeleteUsers(string id)
        {
            return DeleteAsync(id);
        }
    }
}