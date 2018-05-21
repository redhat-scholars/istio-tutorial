using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using System.Net;

namespace dotnet.Controllers
{
    [Route("/")]
    public class ValuesController : Controller
    {
        const string url = "http://recommendation:8080";
        const string responseStringFormat = "preference => {0}\n";
        // GET api/values
        [HttpGet]
        public string Get()
        {
            string hostname = Dns.GetHostName();
            return String.Format(responseStringFormat, hostname);
        }
    }
}
