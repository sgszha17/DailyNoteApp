namespace DailyNoteService.Migrations
{
    using System;
    using System.Collections.Generic;
    using System.Data.Entity.Infrastructure.Annotations;
    using System.Data.Entity.Migrations;

    public partial class AddUserTable : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.Users",
                c => new
                {
                    Email = c.String(),
                    UserName = c.String(),
                    UserID = c.String(nullable: false, maxLength: 128,
                            annotations: new Dictionary<string, AnnotationValues>
                            {
                                {
                                    "ServiceTableColumn",
                                    new AnnotationValues(oldValue: null, newValue: "UserID")
                                },
                            }),
                           
                    RegisterType = c.Int(nullable: null,
                            annotations: new Dictionary<string, AnnotationValues>
                            {
                                {
                                    "ServiceTableColumn",
                                    new AnnotationValues(oldValue: null, newValue: "RegisterType")
                                },
                            }),
                    CreatedAt = c.DateTimeOffset(nullable: false, precision: 7,
                            annotations: new Dictionary<string, AnnotationValues>
                            {
                                {
                                    "ServiceTableColumn",
                                    new AnnotationValues(oldValue: null, newValue: "CreatedAt")
                                },
                            }),
                    UpdatedAt = c.DateTimeOffset(precision: 7,
                            annotations: new Dictionary<string, AnnotationValues>
                            {
                                {
                                    "ServiceTableColumn",
                                    new AnnotationValues(oldValue: null, newValue: "UpdatedAt")
                                },
                            }),
                    UseGoogle = c.String(),

                    UseFB = c.String(),

                    UseWechat = c.String(),

                    AdditionalData = c.String(),

    })
                .PrimaryKey(t => t.Email)
                .Index(t => t.CreatedAt, clustered: true);

        }

        public override void Down()
        {
            DropIndex("dbo.Users", new[] { "CreatedAt" });
            DropTable("dbo.Users",
                removedColumnAnnotations: new Dictionary<string, IDictionary<string, object>>
                {
                    {
                        "CreatedAt",
                        new Dictionary<string, object>
                        {
                            { "ServiceTableColumn", "CreatedAt" },
                        }
                    },
                    {
                        "Deleted",
                        new Dictionary<string, object>
                        {
                            { "ServiceTableColumn", "Deleted" },
                        }
                    },
                    {
                        "UserID",
                        new Dictionary<string, object>
                        {
                            { "ServiceTableColumn", "UserID" },
                        }
                    },
                    {
                        "UpdatedAt",
                        new Dictionary<string, object>
                        {
                            { "ServiceTableColumn", "UpdatedAt" },
                        }
                    },
                });
        }
    }
}