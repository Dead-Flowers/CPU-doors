# Generated by Django 5.0.1 on 2024-01-07 16:34

from django.db import migrations


class Migration(migrations.Migration):
    dependencies = [
        ("controllers", "0004_alter_controllerdevice_unique_together_and_more"),
    ]

    operations = [
        migrations.RemoveField(
            model_name="controllerdevice",
            name="user",
        ),
    ]
